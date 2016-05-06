package com.aleiye.lassock.live.hill.source;

/**
 * 事件追踪采集
 * 
 * @author ruibing.zhao
 * @since 2016年4月14日
 * @version 1.0
 */
public interface EventTrackSource extends Source {
	/**
	 * 采集数据
	 * 
	 * @throws Exception
	 */
	public void pick() throws Exception;
}
