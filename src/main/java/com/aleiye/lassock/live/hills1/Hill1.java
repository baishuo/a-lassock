package com.aleiye.lassock.live.hills1;

import java.util.List;
import java.util.Map;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.common.able.Destroyable;
import com.aleiye.lassock.live.basket.Basket;

/**
 * 采集源接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public interface Hill1 extends Destroyable {

	void initialize() throws Exception;

	public void setBaskets(Map<String, Basket> basket);

	// 增加课程
	void addCourse(Course course) throws Exception;

	// 批量增加课程
	void addCourse(List<Course> courses) throws Exception;

	// 修改课程
	void modifyCourse(Course course) throws Exception;

	// 移除课程

	void removeCourse(Course course) throws Exception;

	// 暂停
	void pause();

	// 恢复
	void resume();

	boolean isPaused();

	List<Intelligence> getIntelligences();
}
