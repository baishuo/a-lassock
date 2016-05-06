package com.aleiye.lassock.live.hill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.api.LassockState.RunState;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.exception.CourseException;
import com.aleiye.lassock.live.exception.SignException;
import com.aleiye.lassock.live.hill.executor.tool.SourceExecutor;
import com.aleiye.lassock.live.hill.executor.tool.SourceScheduler;
import com.aleiye.lassock.live.hill.source.DefaultSourceFactory;
import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.SourceRunner;
import com.aleiye.lassock.live.hill.source.text.CustomThreadPoolExecutor;
import com.aleiye.lassock.live.hills1.Shade;
import com.aleiye.lassock.live.mark.Marker;
import com.aleiye.lassock.live.station.BasketStation;
import com.aleiye.lassock.util.ClassUtils;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.MarkUtil;
import com.aleiye.lassock.util.ScrollUtils;
import com.google.common.eventbus.Subscribe;

/**
 * 采集图
 * 
 * @author ruibing.zhao
 * @since 2015年6月4日
 * @version 2.1.2
 */
public class DefaultHill implements Hill {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHill.class);
	protected LassockState state;
	// 所有 对列
	protected BasketStation baskets;
	// 采集子源工厂
	DefaultSourceFactory factory = new DefaultSourceFactory();
	// 暂停开关
	protected AtomicBoolean paused = new AtomicBoolean(false);
	// 是否消毁
	protected AtomicBoolean destroyed = new AtomicBoolean(false);

	// 所有课程
	protected ConcurrentHashMap<String, Course> courses = new ConcurrentHashMap<String, Course>();
	// 课程对应采集
	protected Map<String, SourceRunner> sourceRunners = new HashMap<String, SourceRunner>();

	@Override
	public void setBaskets(BasketStation baskets) {
		this.baskets = baskets;
	}

	Marker<Long> marker = null;
	Timer timer;

	@Override
	public void initialize() throws Exception {
		if (destroyed.compareAndSet(false, true)) {
			state = new LassockState();
			state.setInformation(Sistem.getInformation());
			SourceExecutor.start();
			SourceScheduler.start();
			// ShadeFileExecutor.start();
			// 标记初始化
			if (ConfigUtils.getConfig().getBoolean("marker.enabled")) {
				marker = ClassUtils.newInstance(ConfigUtils.getConfig().getString("marker.class"));
				marker.load();
				timer = new Timer("marker_timer");
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						try {
							marker.save();
						} catch (Exception e) {
							LOGGER.info("mark save is failure!", e);
						}
					}
				};
				timer.schedule(tt, 10000, ConfigUtils.getConfig().getLong("marker.period"));
				MarkUtil.setMarker(marker);
			}
		}
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

	@Subscribe
	public void changRunType(RunState state) {
		if (state == RunState.RUNNING) {
			resume();
		} else if (state == RunState.PAUSED) {
			pause();
		}
	}

	@Subscribe
	public synchronized void putAll(List<Course> courses) throws Exception {
		if (courses == null || courses.size() == 0) {
			return;
		}
		for (Course course : courses) {
			put(course);
		}
	}

	@Subscribe
	@Override
	public synchronized void put(Course course) throws Exception {
		remove(course.getName());
		ScrollUtils.validate(course);
		if (!this.paused.get()) {
			String type = course.getType().toString();
			Source source = factory.create(course.getName(), type);
			source.setBasket(baskets.getBasket(course.getBasketName()));
			source.configure(course);
			SourceRunner runner = SourceRunner.forSource(source);
			runner.start();
			sourceRunners.put(course.getName(), runner);
		}
		courses.put(course.getName(), course);
		state.setScrollCount(state.getScrollCount() + 1);
	}

	@Subscribe
	@Override
	public synchronized void remove(String course) throws Exception {
		Course existsCourse = courses.remove(course);
		if (existsCourse != null) {
			SourceRunner runner = sourceRunners.remove(existsCourse.getName());
			if (runner != null)
				runner.stop();
		}
		state.setScrollCount(state.getScrollCount() - 1);
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
			putAll(new ArrayList<Course>(oldCourses.values()));
		} catch (Exception e) {
			;
		}
		LOGGER.info("Collect resumed!");
		state.setState(RunState.RUNNING);
	}

	@Override
	public synchronized void pause() {
		if (!paused.compareAndSet(false, true)) {
			IllegalStateException error = new IllegalStateException();
			LOGGER.error("Cannot be paused more than once", error);
			throw error;
		}
		// 关闭所有Shade
		for (SourceRunner shade : sourceRunners.values()) {
			if (shade.getLifecycleState() == LifecycleState.START)
				shade.stop();
		}
		// 清空Shade
		sourceRunners.clear();
		LOGGER.info("Collect paused!");
		state.setState(RunState.PAUSED);
	}

	@Override
	public boolean isPaused() {
		return this.paused.get();
	}

	public synchronized void destroy() {
		if (destroyed.compareAndSet(true, false)) {
			SourceExecutor.shutdown();
			SourceScheduler.shutdown(true);
			// ShadeFileExecutor.shutdown();
			CustomThreadPoolExecutor executor = CustomThreadPoolExecutor.newExecutor();
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
			// 定时关闭
			if (timer != null) {
				timer.cancel();
			}
			// 标记关闭
			CloseableUtils.closeQuietly(marker);
			state.setState(RunState.SHUTDOWN);
		}
	}

	@Override
	public synchronized List<Intelligence> getIntelligences() {
		List<Intelligence> intelligences = new ArrayList<Intelligence>();
		for (SourceRunner runner : sourceRunners.values()) {
			intelligences.add(runner.getShade().getIntelligence());
		}
		return intelligences;
	}

	@Override
	public LassockState getState() {
		return state;
	}
}
