package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.common.NamedComponent;
import com.aleiye.lassock.common.able.Configurable;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.model.Mushroom;

/**
 * 采集缓存对列(竹篮)
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.2
 */
public interface Basket extends LifecycleAware, NamedComponent, Configurable {

	/**
	 * 放入
	 * 
	 * @param mushroom
	 * @throws InterruptedException
	 */
	void push(Mushroom mushroom) throws InterruptedException;

	/**
	 * 取出
	 * 
	 * @return
	 */
	Mushroom take() throws InterruptedException;
}
