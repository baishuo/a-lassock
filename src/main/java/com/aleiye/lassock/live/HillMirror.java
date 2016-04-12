package com.aleiye.lassock.live;

import java.util.List;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.api.LassockState.RunState;
import com.aleiye.lassock.api.shade.CourseType;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.hill.DefaultHill;
import com.aleiye.lassock.live.hill.Hill;
import com.aleiye.lassock.live.hills1.Hill1;
import com.aleiye.lassock.live.hills1.text.TextHill;
import com.aleiye.lassock.live.station.BasketStation;
import com.aleiye.lassock.util.DestroyableUtils;

/**
 * hill镜子类，用于共存旧版Hill和新版hill
 * 
 * @author ruibing.zhao
 * @since 2016年2月16日
 * @version 1.0
 */
public class HillMirror implements Hill {

	/** 队列*/
	private BasketStation baskets;

	private Hill1 hill1;

	private Hill hill;

	protected LassockState state;

	@Override
	public void resume() {
		hill.resume();
		hill1.resume();
		state.setState(RunState.RUNNING);
	}

	@Override
	public void pause() {
		hill1.pause();
		hill.pause();
		state.setState(RunState.PAUSED);
	}

	@Override
	public boolean isPaused() {
		return hill.isPaused();
	}

	@Override
	public void refresh(List<Course> curriculum) throws Exception {
		for (Course course : curriculum) {
			add(course);
		}

	}

	// @Override
	// public void clean() {
	// hill.clean();
	//
	// }
	//
	// @Override
	// public void clean(String type) {
	// hill.clean(type);
	//
	// }
	//
	// @Override
	// public void clean(String type, String subType) {
	// hill.clean(type, subType);
	// }

	@Override
	public synchronized void add(Course course) throws Exception {
		if (course.getType() == CourseType.TEXT)
			hill1.addCourse(course);
		else
			hill.add(course);
		state.setScrollCount(state.getScrollCount() + 1);

	}

	@Override
	public void modify(Course course) throws Exception {
		if (course.getType() == CourseType.TEXT)
			hill1.modifyCourse(course);
		else
			hill.modify(course);

	}

	@Override
	public synchronized void remove(Course course) throws Exception {
		if (course.getType() == CourseType.TEXT)
			hill1.removeCourse(course);
		else
			hill.remove(course);
		state.setScrollCount(state.getScrollCount() - 1);

	}

	@Override
	public void destroy() {
		DestroyableUtils.destroyQuietly(hill);
		DestroyableUtils.destroyQuietly(hill1);
		state.setState(RunState.SHUTDOWN);
	}

	@Override
	public void initialize() throws Exception {
		hill = (Hill) new DefaultHill();
		hill.setBaskets(baskets);
		hill.initialize();

		hill1 = new TextHill();
		hill1.setBaskets(baskets);
		hill1.initialize();

		state = new LassockState();
		state.setInformation(Sistem.getInformation());
	}

	@Override
	public void setBaskets(BasketStation baskets) {
		this.baskets = baskets;
	}

	@Override
	public List<Intelligence> getIntelligences() {
		List<Intelligence> list = hill.getIntelligences();
		list.addAll(hill1.getIntelligences());
		return list;
	}

	@Override
	public LassockState getState() {
		return state;
	}
}
