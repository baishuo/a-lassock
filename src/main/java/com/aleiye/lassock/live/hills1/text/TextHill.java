package com.aleiye.lassock.live.hills1.text;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseConst;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.CourseException;
import com.aleiye.lassock.live.exception.SignException;
import com.aleiye.lassock.live.hills1.AbstractHill;
import com.aleiye.lassock.live.hills1.Shade;
import com.aleiye.lassock.live.hills1.text.TextShade.Stat;
import com.aleiye.lassock.live.mark.Marker;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.MarkUtil;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * 事件驱动采集图
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.1
 */
public class TextHill extends AbstractHill<TextSign, TextShade> {


	// 等待执行队列(单元对应Shade)
	private final BlockingQueue<TextShade> normals = new LinkedBlockingQueue<TextShade>();

	// 发生错误的Shade
	private final BlockingQueue<TextShade> errors = new LinkedBlockingQueue<TextShade>();

	// 等待执行队列(单元对应Shade)
	private final List<TextShade> idles = Collections.synchronizedList(new LinkedList<TextShade>());

	// 应急备用通道(用于在关闭时保存唤醒阻塞无处存放的Shade)
	private final List<TextShade> emergency = Collections.synchronizedList(new LinkedList<TextShade>());

	// 采集执行器
	private CustomThreadPoolExecutor executor;

	/**
	 * 巡检线程(定时检查新事件)
	 */
	private Thread t110 = new Thread(new Runnable() {
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					refreshCourse();
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					logger.info(Thread.currentThread().getName() + "线程停止!");
				}
			}
		}
	}, "t110");

	/**
	 * 消防检查线程(当事件停止时作检查) 
	 * 该线程负责可读队列和空闲队列
	 */
	private Thread t119 = new Thread(new Runnable() {
		// 空闲检查
		private void checkIdles() throws InterruptedException {
			synchronized (idles) {
				for (int i = 0; i < idles.size(); i++) {
					TextShade shade = idles.get(i);
					try {
						shade.open();
						if (shade.getStat() != Stat.END) {
							normals.put(shade);
							idles.remove(i);
						} else {
							IOUtils.closeQuietly(shade);
						}
					} catch (Exception e) {
						errors.add(shade);
					}
				}
			}
		}

		@Override
		public void run() {
			// 控制时间
			long time = System.currentTimeMillis();
			// 获取Shade
			TextShade shade = null;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// 每隔1分种检查空闲
					long curTime = System.currentTimeMillis();
					if ((curTime - time) > 60000) {
						checkIdles();
						time = System.currentTimeMillis();
						continue;
					}
					// 获取一个Shade
					shade = normals.poll(1000, TimeUnit.MILLISECONDS);
					if (shade == null) {
						continue;
					}
					// 读取结束,自检
					if (shade.getStat() == Stat.END) {
						shade.selfCheck();
					}
					// 正常时放入执行队列
					if (shade.getStat() == Stat.NORMAL) {
						executor.execute(shade);
					}
					// 依旧为读取结束时
					else if (shade.getStat() == Stat.END) {
						// 文件以变更
						if (shade.getSign().isChanged()) {
							synchronized (courseLock) {
								// 依旧续读的ID数量
								int zoreCount = 0;
								// 变更后采集次数自增1
								int count = shade.getSign().getChangedReadCount() + 1;
								// 变更后检查次数
								shade.getSign().setChangedReadCount(count);
								List<String> courseIds = new ArrayList<String>();
								courseIds.addAll(shade.getSign().getCourseIdList());
								// 先从移动目录和本目录找
								for (String s : courseIds) {
									Course course = courses.get(s);
									if (course != null) {
										int prentChangedReadCount = Integer.parseInt(course.get(
												CourseConst.text.CHANGED_READ_COUNT).toString());
										if (prentChangedReadCount == 0) {
											zoreCount++;
											continue;
										}
										if (count >= prentChangedReadCount) {
											shade.getSign().disassociate(s);
											List<TextSign> runits = details.get(course.getName());
											for (int i = 0; i < runits.size(); i++) {
												TextSign u = runits.get(i);
												if (u.getId().equals(shade.getSign().getId())) {
													runits.remove(i);
													break;
												}
											}
										}
									} else {
										shade.getSign().disassociate(s);
									}
								}
								if (shade.getSign().getCourseIdList().size() == 0) {
									shade.setStat(Stat.REMOVED);
									errors.add(shade);
									continue;
								} else if (zoreCount == shade.getSign().getCourseIdList().size()) {
									shade.getSign().setChanged(false);
									shade.getSign().setChangedReadCount(0);
								}
							}
						}
						// 60次自检没有可读数据时 移动到空闲集
						if (shade.getEndConut() > 60) {
							CloseableUtils.closeQuietly(shade);
							idles.add(shade);
						} else {
							boolean isOffer = normals.offer(shade, 60000, TimeUnit.MILLISECONDS);
							if (!isOffer) {
								CloseableUtils.closeQuietly(shade);
								idles.add(shade);
							}
						}
					}
					// 其它当异常Shade处理
					else {
						errors.put(shade);
					}
				} catch (InterruptedException e) {
					if (shade != null) {
						emergency.add(shade);
					}
					logger.info(Thread.currentThread().getName() + "线程停止!");
				}
			}
		}
	}, "t119");

	/**
	 * 医生线程(事件发生异常时修正或移除) 
	 * 负责异常对列检查和修复
	 */
	Thread t120 = new Thread(new Runnable() {
		@Override
		public void run() {
			TextShade shade = null;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// 从异常队列获取一个Shade
					shade = errors.poll(1000, TimeUnit.MILLISECONDS);
					if (shade == null) {
						continue;
					}
					if (shade.getSign().isRemoved()) {
						CloseableUtils.closeQuietly(shade);
						continue;
					}
					if (shade.getStat() == Stat.ERR) {
						TextSign unit = shade.getSign();
						String fileKey = unit.getNodeId();
						// 文件验证
						File file = new File(unit.getPath());
						// fileKey 取得（sign 为INODE,WINDOW为文件绝对路径）
						String changedKey = FileGeter.getFileKey(file);
						// 如果当前fileKey 和 取得FileKey相同
						if (fileKey.equals(changedKey)) {
							// 可读
							if (file.isFile() && file.canRead()) {
								shade.setStat(Stat.END);
							} else {
								shade.setStat(Stat.REMOVED);
							}
						} else {
							// 根据所有Course配置查找该文件
							synchronized (courseLock) {
								List<String> courseIds = unit.getCourseIdList();
								List<String> removedIdList = unit.getCourseIdList();
								Set<Course> currCourses = new HashSet<Course>();
								Set<String> paths = new HashSet<String>();
								// 先从移动目录和本目录找
								for (String s : courseIds) {
									Course course = courses.get(s);
									if (course != null) {
										String mp = course.getString(CourseConst.text.MOVE_PATH);
										if (StringUtils.isNotBlank(mp))
											paths.add(mp);
										currCourses.add(course);
									} else {
										removedIdList.add(s);
									}
								}
								paths.add(file.getParent());
								String newPath = null;
								for (String s : paths) {
									newPath = FileGeter.getFile(s, unit.getId());
									if (newPath != null) {
										break;
									}
								}
								// 移动目录和本目录没找到，重新扫描课程表寻找
								if (newPath == null) {
									for (Course course : currCourses) {
										String reg = course.getString(CourseConst.text.PATH_FILTER_REGEX);
										FileFinder ff = new FileFinder(new String[] {
											reg
										});
										List<File> findFiles = ff.getFiles(course
												.getString(CourseConst.text.DATA_INPUT_PATH));
										for (File file1 : findFiles) {
											String findKey = FileGeter.getFileKey(file1);
											if (fileKey.equals(findKey)) {
												newPath = file1.getPath();
												break;
											}
										}
										if (newPath != null) {
											break;
										}
									}
								}
								// 找到文件
								if (newPath != null) {
									File newFile = new File(newPath);
									if (newFile.isFile() && newFile.canRead()) {
										BasicFileAttributes bfa1 = FileGeter.getFileAttributes(newFile);
										// 创建时间一样表时同一文件
										if (shade.getSign().getCt() == bfa1.creationTime().toMillis()) {
											unit.setPath(newPath);
											unit.setChanged(true);
											shade.setStat(Stat.END);
										}
										// 创建时间不同,已不是同一文件
										else {
											shade.setStat(Stat.REMOVED);
										}
									}
									// 不是文件或不能读
									else {
										shade.setStat(Stat.REMOVED);
									}
								}
								// 没有找到文件
								else {
									shade.setStat(Stat.REMOVED);
								}
							}
						}
					}
					if (shade.getStat() == Stat.END || shade.getStat() == Stat.NORMAL) {
						normals.put(shade);
						continue;
					}
					// 如果是移称状态
					if (shade.getStat() == Stat.REMOVED) {
						removeSign(shade.getSign());
					}
				} catch (InterruptedException e1) {
					if (shade != null)
						emergency.add(shade);
					logger.info(Thread.currentThread().getName() + "线程停止!");
				} catch (Exception e) {
					if (shade != null) {
						try {
							errors.put(shade);
						} catch (InterruptedException e1) {
							emergency.add(shade);
							logger.error(Thread.currentThread().getName() + "线程停止!");
						}
					}
				}
			}
		}
	}, "t120");

	Marker<Long> marker = null;
	Timer timer;
	boolean markerEnabled = false;

	@Override
	public void initialize() throws Exception {
		// 初始化执行线程
		executor = CustomThreadPoolExecutor.newExecutor(this.normals, this.errors, this.emergency);
		// 巡检开启
		t110.start();
		// 消防开启
		t119.start();
		// 医生开启
		t120.start();
	}

	/**
	 * 刷新
	 */
	protected void refreshCourse() {
		try {
			List<TextSign> unitList = new ArrayList<TextSign>();
			synchronized (courseLock) {
				for (Course course : courses.values()) {
					List<TextSign> rfu = details.get(course.getName());
					if (rfu == null) {
						rfu = new ArrayList<TextSign>();
					}
					Set<TextSign> oldUnits = Sets.newHashSet(rfu.iterator());

					// 扫描课程扫描采集集
					List<TextSign> units = makeDetails(course);
					// 扫描课程扫描采集集
					Set<TextSign> newUnits = Sets.newHashSet(units.iterator());

					// 新旧差值
					SetView<TextSign> addUnits = Sets.difference(newUnits, oldUnits);
					for (TextSign unit : addUnits) {
						unit.associate(course.getName());
						unitList.add(unit);
					}
					details.put(course.getName(), units);
				}
				// 添加差值Unit
				for (TextSign unit : unitList) {
					addSign(unit);
				}
			}
		} catch (Exception e) {
			logger.error("刷新Text 失败!", e);
		}
	}

	/**
	 * 重写父类
	 * 屏闭父类操作，因为本HILL有定时巡检线程刷新Course
	 */
	@Override
	protected void afterAddCourse(Course course) {}

	/**
	 * 根据课程扫描采集清单
	 */
	@Override
	protected List<TextSign> makeDetails(Course course) {
		List<TextSign> details = new ArrayList<TextSign>();
		String reg = course.getString(CourseConst.text.PATH_FILTER_REGEX);
		// 创建FILE FINDER
		FileFinder ff = new FileFinder(new String[] {
			reg
		});
		// 获取所有可读文件
		List<File> findFiles = ff.getFiles(course.getString(CourseConst.text.DATA_INPUT_PATH));
		// 创建Units
		for (File file : findFiles) {
			BasicFileAttributes bfa = FileGeter.getFileAttributes(file);
			if (bfa == null) {
				break;
			}
			TextSign sign = new TextSign();
			sign.setType(course.getType().toString());
			sign.setNodeId(FileGeter.getFileKey(file, bfa));
			sign.setId(course.getType() + ":" + sign.getNodeId());
			sign.setName(sign.getId());
			sign.setCt(bfa.creationTime().toMillis());
			sign.setLmt(bfa.lastModifiedTime().toMillis());
			sign.setPath(file.getPath());
			sign.setCourse(course);
			details.add(sign);
		}
		return details;
	}

	@Override
	protected TextShade creatShade(TextSign sign) {
		Basket defaultBasket = baskets.getBasket("_DEFAULT");
		Basket basket = defaultBasket;
		if (StringUtils.isNotBlank(sign.getBasketName())) {
			if (baskets.contains(sign.getBasketName())) {
				basket = baskets.getBasket(sign.getBasketName());
			}
		}
		FileShade fs = new FileShade(sign, basket);
		long offset = MarkUtil.getMark(sign.getId());
		fs.seek(offset);
		return fs;
	}

	@Override
	protected void afterAddShade(TextShade shade) throws Exception {
		if (shade.getStat() == Stat.NORMAL || shade.getStat() == Stat.END) {
			normals.put(shade);
		} else {
			errors.put(shade);
		}
	}

	@Override
	protected void handleException(Exception e) throws SignException, CourseException {
		throw new SignException(e.getMessage());
	}

	@Override
	protected boolean compareCourse(Course newc, Course old) {
		if (!newc.getString(CourseConst.text.DATA_INPUT_PATH).equals(old.getString(CourseConst.text.DATA_INPUT_PATH))) {
			return false;
		}
		if (!newc.getString(CourseConst.text.PATH_FILTER_REGEX).equals(
				old.getString(CourseConst.text.PATH_FILTER_REGEX))) {
			return false;
		}
		if (!newc.getString(CourseConst.text.PATH_FILTER_REGEX).equals(
				old.getString(CourseConst.text.PATH_FILTER_REGEX))) {
			return false;
		}
		if (!newc.getString(CourseConst.text.MOVE_PATH).equals(old.getString(CourseConst.text.MOVE_PATH))) {
			return false;
		}
		return true;
	}

	@Override
	protected void validateCourse(Course course) throws Exception {
		if (StringUtils.isBlank(course.getString(CourseConst.text.DATA_INPUT_PATH))) {
			throw new CourseException("Date input path no be null!");
		}
	}

	@Override
	public void destroy() {
		if (!destroyed.get()) {
			// 关闭巡检
			t110.interrupt();
			// 关闭消防
			t119.interrupt();
			// 关闭医院
			t120.interrupt();
			executor.stoped();
			executor.resume();
			executor.shutdown();
			try {
				executor.awaitTermination(60000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				List<Runnable> runs = executor.shutdownNow();
				for (Runnable r : runs) {
					Shade s = (Shade) r;
					CloseableUtils.closeQuietly(s);
				}
			}
			// 关闭正常Shade队列
			Iterator<TextShade> ite = normals.iterator();
			while (ite.hasNext()) {
				Shade s = ite.next();
				if (s.isOpen()) {
					CloseableUtils.closeQuietly(s);
				}
			}
			normals.clear();
			// 关闭异常Shade队列
			ite = errors.iterator();
			while (ite.hasNext()) {
				Shade s = ite.next();
				if (s.isOpen()) {
					CloseableUtils.closeQuietly(s);
				}
			}
			errors.clear();
			// 关闭紧急通道Shade集
			ite = emergency.iterator();
			while (ite.hasNext()) {
				Shade s = ite.next();
				if (s.isOpen()) {
					CloseableUtils.closeQuietly(s);
				}
			}
			emergency.clear();
			idles.clear();

			// 定时关闭
			if (timer != null) {
				timer.cancel();
			}
			// 标记关闭
			if (markerEnabled) {
				CloseableUtils.closeQuietly(marker);
			}
			super.destroy();
		}
	}

	@Override
	public void pause() {
		executor.pause();
	}

	@Override
	public void resume() {
		executor.resume();

	}

	@Override
	public boolean isPaused() {
		return executor.isPaused();

	}
}
