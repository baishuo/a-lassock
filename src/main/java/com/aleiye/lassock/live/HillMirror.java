package com.aleiye.lassock.live;

import java.util.List;
import java.util.Map;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.hill.DefaultHill;
import com.aleiye.lassock.live.hill.Hill;
import com.aleiye.lassock.live.scroll.Course;
import com.aleiye.lassock.live.scroll.Course.RunType;
import com.aleiye.lassock.util.DestroyableUtils;
import com.aleiye.lassock1.live.hills.Hill1;
import com.aleiye.lassock1.live.hills.text.TextHill;

/**
 * 
 * 
 * @author ruibing.zhao
 * @since 2016年2月16日
 * @version 1.0
 */
public class HillMirror implements Hill {

	/** 队列*/
	private Map<String, Basket> baskets;

	private Hill1 hill1;

	private Hill hill;

	@Override
	public void resume() {
		hill.resume();

	}

	@Override
	public void pause() {
		hill.pause();

	}

	@Override
	public boolean isPaused() {
		return hill.isPaused();
	}

	@Override
	public void refresh(List<Course> curriculum) throws Exception {
		for (Course course : curriculum) {
			if (course.getRunType() == RunType.TEXT)
				hill1.addCourse(course);
			else
				hill.add(course);
		}

	}

	@Override
	public void clean() {
		hill.clean();

	}

	@Override
	public void clean(String type) {
		hill.clean(type);

	}

	@Override
	public void clean(String type, String subType) {
		hill.clean(type, subType);

	}

	@Override
	public void add(Course course) throws Exception {
		if (course.getRunType() == RunType.TEXT)
			hill1.addCourse(course);
		else
			hill.add(course);

	}

	@Override
	public void modify(Course course) throws Exception {
		if (course.getRunType() == RunType.TEXT)
			hill1.modifyCourse(course);
		else
			hill.modify(course);

	}

	@Override
	public void remove(Course course) throws Exception {
		if (course.getRunType() == RunType.TEXT)
			hill1.removeCourse(course);
		else
			hill.remove(course);

	}

	@Override
	public void destroy() throws Exception {
		DestroyableUtils.destroyQuietly(hill);
		DestroyableUtils.destroyQuietly(hill1);
	}

	@Override
	public void initialize() throws Exception {
		hill = (Hill) new DefaultHill();
		hill.setBaskets(baskets);
		hill.initialize();

		hill1 = new TextHill();
		hill1.setBasket(baskets.get("simple"));
		hill1.initialize();
	}

	@Override
	public void setBaskets(Map<String, Basket> baskets) {
		this.baskets = baskets;
	}

}
