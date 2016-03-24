package com.aleiye.lassock.liveness;

import com.aleiye.lassock.common.able.Configurable;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.Live;

/**
 * 课程监听器接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月11日
 * @version 2.2.1
 */
public interface Liveness extends LifecycleAware, Configurable {
	void setLive(Live live);
}
