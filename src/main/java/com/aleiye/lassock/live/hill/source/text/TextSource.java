package com.aleiye.lassock.live.hill.source.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.hill.source.AbstractEventDrivenSource;
import com.aleiye.lassock.live.hill.source.text.cluser.Cluser;
import com.aleiye.lassock.live.hill.source.text.cluser.CluserListener;
import com.aleiye.lassock.live.hill.source.text.cluser.CluserState;
import com.aleiye.lassock.live.hill.source.text.cluser.FileCluser;
import com.aleiye.lassock.live.hill.source.text.cluser.TextCluser;
import com.aleiye.lassock.live.mark.Marker;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.util.ClassUtils;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.MarkUtil;
import com.aleiye.lassock.util.ScrollUtils;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * 事件驱动采集图
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.1
 */
public class TextSource extends AbstractEventDrivenSource {
	private final Logger logger = LoggerFactory.getLogger(TextSource.class);

	TextParam param;
	// 采集执行器
	private CustomThreadPoolExecutor executor;

	// 等待执行队列(单元对应Shade)
	private final BlockingQueue<TextCluser> normals = new LinkedBlockingQueue<TextCluser>();

	// 发生错误的Shade
	private final BlockingQueue<TextCluser> errors = new LinkedBlockingQueue<TextCluser>();

	// 等待执行队列(单元对应Shade)
	private final List<TextCluser> idles = Collections.synchronizedList(new LinkedList<TextCluser>());

	// 应急备用通道(用于在关闭时保存唤醒阻塞无处存放的Shade)
	private final List<TextCluser> emergency = Collections.synchronizedList(new LinkedList<TextCluser>());

	Marker<Long> marker = null;
	Timer timer;
	boolean markerEnabled = false;

	FileMonitor fileMonitor;

	@Override
	protected void doConfigure(Course cource) throws Exception {
		param = ScrollUtils.forParam(cource, TextParam.class);
	}

	@Override
	protected void doStart() throws Exception {
		// 初始化执行线程
		executor = CustomThreadPoolExecutor.newExecutor();
		// 初始化文件监听
		fileMonitor = new FileMonitor(new FileListener() {
			@Override
			public void fileDeleted(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileCreated(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileChanged(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}
		});
		fileMonitor.start();

		// 创建FILE FINDER
		FileFinder ff = new FileFinder(new String[] {
			param.getPathFilterRegex()
		});
		// 获取所有可读文件
		List<File> findFiles = ff.getFiles(param.getPath());

		// 标记初始化
		if (markerEnabled = ConfigUtils.getConfig().getBoolean("marker.enabled")) {
			marker = ClassUtils.newInstance(ConfigUtils.getConfig().getString("marker.class"));
			marker.load();
			timer = new Timer("marker_timer");
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					try {
						marker.save();
					} catch (Exception e) {
						logger.info("mark save is failure!", e);
					}
				}
			};
			timer.schedule(tt, 10000, ConfigUtils.getConfig().getLong("marker.period"));
			MarkUtil.setMarker(marker);
		}
	}

	protected void doStop() throws Exception {
		executor.stoped();
		executor.resume();
		executor.shutdown();
		try {
			executor.awaitTermination(60000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			List<Runnable> runs = executor.shutdownNow();
			for (Runnable r : runs) {
				TextCluser s = (TextCluser) r;
				CloseableUtils.closeQuietly(s);
			}
		}
		// 关闭正常Shade队列
		Iterator<TextCluser> ite = normals.iterator();
		while (ite.hasNext()) {
			Cluser s = ite.next();
			if (s.isOpen()) {
				CloseableUtils.closeQuietly(s);
			}
		}
		normals.clear();
		// 关闭异常Shade队列
		ite = errors.iterator();
		while (ite.hasNext()) {
			Cluser s = ite.next();
			if (s.isOpen()) {
				CloseableUtils.closeQuietly(s);
			}
		}
		errors.clear();
		// 关闭紧急通道Shade集
		ite = emergency.iterator();
		while (ite.hasNext()) {
			Cluser s = ite.next();
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
	}

	List<File> files;

	public Map<String, FileObject> sfs = new HashMap<String, FileObject>();

	/**
	 * 刷新文件
	 */
	protected void refresh() {
		try {
			List<File> rfu = files;
			if (rfu == null) {
				rfu = new ArrayList<File>();
			}
			Set<File> oldUnits = Sets.newHashSet(rfu.iterator());
			// 扫描课程扫描采集集
			List<File> units = findFiles();
			// 扫描课程扫描采集集
			Set<File> newUnits = Sets.newHashSet(units.iterator());

			SetView<File> addUnits = Sets.difference(newUnits, oldUnits);
			for (File file : addUnits) {
				Cluser cluser = creatCluser(file);
				cluser.open();
				FileObject fo = fileMonitor.addFile(file);
				sfs.put(file.getAbsolutePath(), fo);
			}
		} catch (Exception e) {
			logger.error("刷新Text 失败!", e);
		}
	}

	/**
	 * 根据课程扫描采集清单
	 */
	protected List<File> findFiles() {
		String reg = param.getPathFilterRegex();
		// 创建FILE FINDER
		FileFinder ff = new FileFinder(new String[] {
			reg
		});
		// 获取所有可读文件
		List<File> findFiles = ff.getFiles(param.getPath());
		return findFiles;
	}

	protected TextCluser creatCluser(File file) {
		CluserListener l = new CluserListener() {
			@Override
			public void picked(Mushroom mushroom) throws InterruptedException {
				putMushroom(mushroom);
			}
		};
		TextCluser cluser = new FileCluser(file, l, normals, errors, emergency);
		cluser.seek(MarkUtil.getMark(cluser.getFileKey()));
		return cluser;
	}

	void addFile(File file) {
//		String key = t.getName();
//		synchronized (signLock) {
//			// 已存在相同单元,关联课程ID
//			if (signs.containsKey(key)) {
//				Sign adder = signs.get(key);
//				if (adder != t) {
//					for (String s : t.getCourseIdList()) {
//						// 合并方式为关联该单元的Course ID
//						adder.associate(s);
//					}
//				}
//			}
//			// 不存在，
//			else {
//				// 创建新Shade
//				S s = null;
//				try {
//					s = creatShade(t);
//					// 打开
//					s.open();
//					afterAddShade(s);
//				} catch (Exception e) {
//					handleException(e);
//				}
//				if (s != null) {
//					shades.put(key, s);
//					signs.put(key, t);
//					logger.info("Course " + this.getName() + " add File " + file.getAbsolutePath());
//				}
//			}
//		}
	}

	protected void afterAddShade(TextCluser shade) throws Exception {
		if (shade.getStat() == CluserState.NORMAL || shade.getStat() == CluserState.END) {
			normals.put(shade);
		} else {
			errors.put(shade);
		}
	}
}
