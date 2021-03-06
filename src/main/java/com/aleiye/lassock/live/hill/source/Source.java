package com.aleiye.lassock.live.hill.source;

import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.common.NamedComponent;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.basket.BasketAware;
import com.aleiye.lassock.live.hill.Hill;

/**
 * 采集子源
 * (子源，它存在于<tt>Course</tt> 配置生成的容器[<tt>Hill</tt>]中)
 * <br>
 * 一个采集源包含多个子源 
 *    如:文件采集,一个目录有多个文件可以采集
 * 
 * <p>树阴，蘑茹长在树阴下</p>
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 1.0
 * @see Hill
 */
public interface Source extends LifecycleAware, NamedComponent, CourseConfigurable, BasketAware {
	// 获取标识
	public Sign getSign();

	// 获取采集情报
	public Intelligence getIntelligence();
}
