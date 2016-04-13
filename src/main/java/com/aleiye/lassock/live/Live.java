package com.aleiye.lassock.live;

import java.util.List;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.common.PausedAware;

/**
 * 生涯
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public interface Live extends PausedAware {

	void refresh(List<Course> curriculum) throws Exception;

	void add(Course course) throws Exception;

	void modify(Course course) throws Exception;

	void remove(Course course) throws Exception;

	List<Intelligence> getIntelligences();

	LassockState getState();
}
