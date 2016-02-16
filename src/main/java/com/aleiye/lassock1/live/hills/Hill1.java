package com.aleiye.lassock1.live.hills;

import java.util.List;

import com.aleiye.lassock.common.able.Destroyable;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.scroll.Course;

/**
 * 采集源接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public interface Hill1 extends Destroyable {

	void initialize() throws Exception;

	public void setBasket(Basket basket);

	// 增加课程
	void addCourse(Course course) throws Exception;

	// 批量增加课程
	void addCourse(List<Course> courses) throws Exception;

	// 修改课程
	void modifyCourse(Course course) throws Exception;

	// 移除课程
	void removeCourse(Course course) throws Exception;

}
