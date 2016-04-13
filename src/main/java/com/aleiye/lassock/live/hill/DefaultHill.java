package com.aleiye.lassock.live.hill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.exception.CourseException;
import com.aleiye.lassock.live.exception.SignException;
import com.aleiye.lassock.live.hill.executor.tool.ShadeExecutor;
import com.aleiye.lassock.live.hill.executor.tool.ShadeScheduler;
import com.aleiye.lassock.live.hill.shade.DefaultShadeFactory;
import com.aleiye.lassock.live.hill.shade.Shade;
import com.aleiye.lassock.live.hill.shade.ShadeRunner;
import com.aleiye.lassock.live.station.BasketStation;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * 采集图
 * 
 * @author ruibing.zhao
 * @since 2015年6月4日
 * @version 2.1.2
 */
public class DefaultHill implements Hill {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHill.class);
	// 所有 对列
	protected BasketStation baskets;
	// 采集子源工厂
	DefaultShadeFactory factory = new DefaultShadeFactory();
	// 暂停开关
	protected AtomicBoolean paused = new AtomicBoolean(false);
	// 是否消毁
	protected AtomicBoolean destroyed = new AtomicBoolean(false);

	// 所有课程
	protected ConcurrentHashMap<String, Course> courses = new ConcurrentHashMap<String, Course>();
	// 采集子源
	protected Map<String, ShadeRunner> shades = new HashMap<String, ShadeRunner>();

	@Override
	public void setBaskets(BasketStation baskets) {
		this.baskets = baskets;
	}

	@Override
	public void initialize() throws Exception {
		ShadeExecutor.start();
		ShadeScheduler.start();
		// ShadeFileExecutor.start();
	}

	/**
	 * 采集子源初始化过程异常处理
	 * 
	 * @param e
	 * @throws SignException
	 *             表示程下多个标识中一个或多个标识发生常异，该常异不会导制程失效
	 * @throws CourseException
	 *             表程单标识代表课程，该异常产生将直接导制课程的失效
	 */
	protected void handleException(Exception e) throws SignException, CourseException {

	}

	@Override
	public synchronized void refresh(List<Course> courses) throws Exception {
		if (courses == null || courses.size() == 0) {
			return;
		}
		for (Course course : courses) {
			add(course);
		}
	}

	@Override
	public synchronized void add(Course course) throws Exception {
		ScrollUtils.validate(course);
		if (!this.paused.get()) {
			try {
				String type = course.getType().toString();
				Shade shade = factory.create(course.getName(), type);
				shade.configure(course);
				String bn = "_DEFAULT";
				if (StringUtils.isNotBlank(course.getBasketName())) {
					bn = course.getBasketName();
				}
				shade.setBasket(baskets.getBasket(bn));
				ShadeRunner runner = ShadeRunner.forSource(shade);
				runner.start();
				shades.put(course.getName(), runner);
			} catch (Exception e) {

			}

		}
		courses.put(course.getName(), course);
	}

	@Override
	public synchronized void remove(Course course) throws Exception {
		Course existsCourse = courses.remove(course.getName());
		if (existsCourse != null) {
			ShadeRunner runner = shades.get(existsCourse.getName());
			if (runner != null)
				runner.stop();
		}
	}

	@Override
	public synchronized void modify(Course course) throws Exception {
		remove(course);
		add(course);
	}

	@Override
	public synchronized void resume() {
		if (!paused.compareAndSet(true, false)) {
			IllegalStateException error = new IllegalStateException();
			LOGGER.error("Cannot be start more than once", error);
			throw error;
		}
		Map<String, Course> oldCourses = courses;
		courses = new ConcurrentHashMap<String, Course>();
		try {
			refresh(new ArrayList<Course>(oldCourses.values()));
		} catch (Exception e) {
			;
		}
		LOGGER.info("Collect resumed!");
	}

	@Override
	public synchronized void pause() {
		if (!paused.compareAndSet(false, true)) {
			IllegalStateException error = new IllegalStateException();
			LOGGER.error("Cannot be paused more than once", error);
			throw error;
		}
		// 关闭所有Shade
		for (ShadeRunner shade : shades.values()) {
			if (shade.getLifecycleState() == LifecycleState.START)
				shade.stop();
		}
		// 清空Shade
		shades.clear();
		LOGGER.info("Collect paused!");
	}

	@Override
	public boolean isPaused() {
		return this.paused.get();
	}

	public synchronized void destroy() {
		if (!destroyed.get()) {
			// this.clean();
			// 关闭type subType Shade
			// // 关闭所有Shade
			// for (ShadeRunner shade : shades.values()) {
			// if (shade.getLifecycleState() == LifecycleState.START)
			// shade.stop();
			// }
			// // 清空Shade
			// shades.clear();
			// // 清空课程
			// courses.clear();
		}
		ShadeExecutor.shutdown();
		ShadeScheduler.shutdown(true);
		// ShadeFileExecutor.shutdown();
		destroyed.set(true);
	}

	@Override
	public synchronized List<Intelligence> getIntelligences() {
		List<Intelligence> intelligences = new ArrayList<Intelligence>();
		for (ShadeRunner runner : shades.values()) {
			intelligences.add(runner.getShade().getIntelligence());
		}
		return intelligences;
	}

	@Override
	public LassockState getState() {
		// TODO Auto-generated method stub
		return null;
	}
}
