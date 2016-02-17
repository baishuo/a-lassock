package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.live.NamedComponent;
import com.aleiye.lassock.live.lifecycle.LifecycleAware;
import com.aleiye.lassock.model.GeneralMushroom;

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
	 * @param generalMushroom
	 * @throws InterruptedException
	 */
	void push(GeneralMushroom generalMushroom) throws InterruptedException;

	/**
	 * 取出
	 * 
	 * @return
	 */
	GeneralMushroom take() throws InterruptedException;
}
