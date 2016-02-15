package com.aleiye.raker.live.basket;

import com.aleiye.raker.live.NamedComponent;
import com.aleiye.raker.live.lifecycle.LifecycleAware;
import com.aleiye.raker.model.Mushroom;

/**
 * 采集缓存对列(竹篮)
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.2
 */
public interface Basket extends LifecycleAware, NamedComponent {

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
