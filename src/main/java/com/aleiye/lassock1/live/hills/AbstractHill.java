package com.aleiye.lassock1.live.hills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.CourseException;
import com.aleiye.lassock.live.exception.SignException;
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.logging.Logging;
import com.aleiye.lassock.util.CloseableUtils;

/**
 * 抽像采集图
 * 
 * @author ruibing.zhao
 * @since 2015年6月4日
 * @version 2.1.2
 */
public abstract class AbstractHill<T extends Sign, S extends AbstractShade<T>> extends Logging implements Hill1 {
	// 是否消毁
	protected final AtomicBoolean destroyed = new AtomicBoolean(false);
	// 输出对列
	protected Map<String, Basket> baskets;

	// 输出注入
	public void setBaskets(Map<String, Basket> baskets) {
		this.baskets = baskets;
	}

	@Override
	public void destroy() {
		if (!destroyed.get()) {
			synchronized (this.courseLock) {
				// 关闭所有Shade
				for (S shade : shades.values()) {
					if (shade.isOpen())
						CloseableUtils.closeQuietly(shade);
				}
				// 清空Shade
				shades.clear();
				// 清空标识
				signs.clear();
				// 清空清单
				details.clear();
				// 清空课程
				courses.clear();
			}
			destroyed.set(true);
		}
	}

	// 课程锁
	protected Object courseLock = new Object();
	// 所有课程
	protected final Map<String, Course> courses = new HashMap<String, Course>();
	// 课程的清单
	protected final Map<String, List<T>> details = new HashMap<String, List<T>>();

	// 标识锁
	protected Object signLock = new Object();
	// 合并后课程单元（共享型单元,多课程拥有同一单元时，将共享同一个单元）
	protected final Map<String, T> signs = new HashMap<String, T>();
	// 采集子源
	protected final Map<String, S> shades = new HashMap<String, S>();

	/*******************************************************************************
	 * 添加操作
	 *******************************************************************************/

	/**
	 * 批量增加课程
	 */
	@Override
	public void addCourse(List<Course> courses) throws Exception {
		if (courses == null || courses.size() == 0) {
			return;
		}
		for (Course course : courses) {
			addCourse(course);
		}
	}

	/**
	 * 增加课程
	 */
	@Override
	public void addCourse(Course course) throws Exception {
		// 验证课程
		validateCourse(course);
		// 锁课程
		synchronized (courseLock) {
			// 课程表里不存在当前课程
			if (!courses.containsKey(course.getName())) {
				// try {
				// 存放课程
				courses.put(course.getName(), course);
				afterAddCourse(course);

				// } catch (CourseException e) {
				// course.setFailure(true);
				// course.setException(e);
				// }
			}
		}
	}

	/**
	 * 增加课程后(用于执于添加标识操作)
	 * 
	 * @param course
	 */
	protected void afterAddCourse(Course course) throws Exception {
		// 制作采集清单
		List<T> signs = makeDetails(course);
		for (T sign : signs) {
			sign.associate(course.getName());
			try {
				addSign(sign);
			} catch (SignException s) {
				// 多标识中某个发生常异处理
				logError(s.getMessage());
			}
		}
		details.put(course.getName(), signs);
	}

	/**
	 * 依据课程制作采集清单
	 * <br>
	 * 清单KEY必须保证唯一性
	 * @param course
	 * @return
	 */
	protected abstract List<T> makeDetails(Course course);

	/**
	 * 添加Unit
	 * 
	 * @param t
	 */
	protected void addSign(T t) throws SignException, CourseException {
		String key = t.getName();
		synchronized (signLock) {
			// 已存在相同单元,关联课程ID
			if (signs.containsKey(key)) {
				Sign adder = signs.get(key);
				if (adder != t) {
					for (String s : t.getCourseIdList()) {
						// 合并方式为关联该单元的Course ID
						adder.associate(s);
					}
				}
			}
			// 不存在，
			else {
				// 创建新Shade
				S s = null;
				try {
					s = creatShade(t);
					// 打开
					s.open();
					afterAddShade(s);
				} catch (Exception e) {
					handleException(e);
				}
				if (s != null) {
					shades.put(key, s);
					signs.put(key, t);
					logInfo("Add sign:" + t.getName());
				}
			}
		}
	}

	/**
	 * 采集子源初始化过程异常处理
	 * @param e
	 * @throws SignException 表示程下多个标识中一个或多个标识发生常异，该常异不会导制程失效
	 * @throws CourseException 表程单标识代表课程，该异常产生将直接导制课程的失效
	 */
	protected abstract void handleException(Exception e) throws SignException, CourseException;

	/**
	 * 创建新的Shade
	 * 
	 * @param sign
	 */
	protected abstract S creatShade(T t) throws Exception;

	/**
	 * 创建Shade成功之后调用,为扩展提供后绪操作
	 * @param shade
	 * @throws Exception
	 */
	protected void afterAddShade(S shade) throws Exception {}

	/*******************************************************************************
	 * 移除操作
	 *******************************************************************************/

	/**
	 * 移除采集课程
	 * @throws Exception 
	 */
	@Override
	public void removeCourse(Course course) throws Exception {
		synchronized (courseLock) {
			Course existsCourse = courses.remove(course.getName());
			if (existsCourse != null) {
				List<T> list = details.remove(course.getName());
				if (list != null && list.size() > 0) {
					synchronized (signLock) {
						for (T sign : list) {
							T removed = this.signs.get(sign.getName());
							removed.disassociate(course.getName());
							if (removed.associateSize() == 0)
								removeSign(removed);
						}
					}
				}
			}
			// else {
			// throw new Exception("Course:" + course.getName() +
			// " don't exist!");
			// }
		}
	}

	/**
	 * 移除Shade
	 * 默认间接移除
	 * 
	 * @param t
	 */
	protected void removeSign(T t) {
		String key = t.getName();
		synchronized (signLock) {
			if (signs.containsKey(key)) {
				T removed = signs.remove(key);
				removed.setRemoved(true);
				removeShade(removed);
				logInfo(this.getClass().getSimpleName() + "/Sign:" + t.getName() + " was removed!");
			}
		}
	}

	/**
	 * 移动一个Shade
	 * @param t
	 */
	private void removeShade(T t) {
		S s = shades.remove(t.getName());
		if (s != null) {
			CloseableUtils.closeQuietly(s);
			afterRemoveShade(s);
		}
	}

	/**
	 * 移除Shade后绪操作
	 * @param unit
	 */
	protected void afterRemoveShade(S shade) {}

	/*******************************************************************************
	 * 更新操作
	 *******************************************************************************/
	/**
	 * 变更课程
	 */
	@Override
	public void modifyCourse(Course course) throws Exception {
		validateCourse(course);
		synchronized (courseLock) {
			Course exist = courses.get(course.getName());
			// if (exist == null) {
			// // 课程不存在的移除是非法的
			// throw new Exception("Course:" + course.getName() +
			// " don't exist!");
			// }
			if (exist != null) {
				// 对比新旧课程，看是否需要更新
				if (compareCourse(course, exist)) {
					// 课程没有需要的变更不被视为真正采集变更
					return;
				}
				// 先删除已存在课程
				removeCourse(course);
			}
			// 再添加新的
			addCourse(course);
		}
	}

	/**
	 * 用于变更时比较新的课程和旧的课程变更点
	 * 
	 * @param newc
	 * @param old
	 * @return
	 */
	protected abstract boolean compareCourse(Course newc, Course old);

	/**
	 * 验证当前课程是否有效
	 * <br>
	 * 添加课程和修改课程时作必要验证
	 * @param course
	 * @return
	 */
	protected abstract void validateCourse(Course course) throws Exception;

	@Override
	public List<Intelligence> getIntelligences() {
		List<Intelligence> list = new ArrayList<Intelligence>();
		synchronized (courseLock) {
			for (Shade shade : shades.values()) {
				list.add(shade.getIntelligence());
			}
		}

		return list;
	}
}
