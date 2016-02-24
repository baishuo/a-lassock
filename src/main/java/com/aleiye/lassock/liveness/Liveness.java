package com.aleiye.lassock.liveness;

import java.io.Closeable;

import com.aleiye.lassock.live.Live;

/**
 * 课程监听器接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月11日
 * @version 2.2.1
 */
public interface Liveness extends Closeable {
	void initialize() throws Exception;

	void lisen(Live live) throws Exception;
}
