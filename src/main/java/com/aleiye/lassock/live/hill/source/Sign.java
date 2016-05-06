package com.aleiye.lassock.live.hill.source;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;

/**
 * 采集标识 <br>
 * 根据课程(<tt>Course</tt>)生成.
 * Sign 针对采集作信息统合
 * <p>
 * 该类为通用采集标识,不同采集类型状态不同时,需继承该类扩展 <tt>Shade</tt>创建必须依赖标识对像,每一个Shade都对应一个唯一标识
 * 
 * @author ruibing.zhao
 * @since 2015年5月20日
 * @version 2.1.2
 * @see Course
 * @see AbstractSource
 */
public class Sign {

	// 任务配置
	private Course course;
	// 运行情报
	protected Intelligence intelligence;

	private Exception exception = null;

	public Sign(Course course) {
		this.course = course;
		this.intelligence = new Intelligence(course.getName());
		this.intelligence.setType(course.getType().toString());
	}

	// 是否移除
	private boolean removed = false;
	// 是否丢失
	private boolean lost = false;

	public String getDescription() {
		return "";
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public boolean isLost() {
		return lost;
	}

	public void setLost(boolean lost) {
		this.lost = lost;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Intelligence getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
