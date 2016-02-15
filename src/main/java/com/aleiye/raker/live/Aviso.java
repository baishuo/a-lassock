package com.aleiye.raker.live;

import com.aleiye.raker.live.scroll.Course;

/**
 * 通报,其实组件通过该接口获取课程相关信息
 * 
 * @author ruibing.zhao
 * @since 2015年6月26日
 * @version 2.1.2
 */
public interface Aviso {
	/**
	 * 获取课程
	 * @param courseId
	 * @return
	 */
	public Course getCourse(String courseId);

	/**
	 * 获取课程传输方式
	 * @param courseId
	 * @return
	 */
	public String getTransportType(String courseId);
}
