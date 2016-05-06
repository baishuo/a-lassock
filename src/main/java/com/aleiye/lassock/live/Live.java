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
	/**
	 * 批量添加采集任务
	 * 
	 * @param curriculum
	 * @throws Exception
	 */
	void putAll(List<Course> curriculum) throws Exception;

	/**
	 * 添加采集任务
	 * 
	 * @param course
	 * @throws Exception
	 */
	void put(Course course) throws Exception;

	/**
	 * 移除任务
	 * 
	 * @param course
	 * @throws Exception
	 */
	void remove(String course) throws Exception;

	/**
	 * 获取采集任务的情报信息
	 * 
	 * @return
	 */
	List<Intelligence> getIntelligences();

	/**
	 * 获取采集状态
	 * 
	 * @return
	 */
	LassockState getState();
}
