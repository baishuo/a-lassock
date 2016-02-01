package com.aleiye.lassock.live;

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

	@Deprecated
	void refresh(List<Course> curriculum) throws Exception;

	void clean();

	void clean(String type);

	void clean(String type, String subType);

	void add(Course course) throws Exception;

	void modify(Course course) throws Exception;

	void remove(Course course) throws Exception;
}
