package com.aleiye.lassock1.live;

import java.util.List;

import com.aleiye.lassock.live.scroll.Course;

/**
 * 生涯
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public interface Live {

	void resume();

	void pause();

	boolean isPaused();

	void refresh(List<Course> curriculum) throws Exception;

	void add(Course course) throws Exception;

	void modify(Course course) throws Exception;

	void remove(Course course) throws Exception;
}
