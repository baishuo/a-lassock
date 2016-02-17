package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.model.GeneralMushroom;

/**
 * 缓存队列列车(竹篮列)
 * 
 * @author ruibing.zhao
 * @since 2015年6月9日
 * @version 2.1.2
 */
public interface BasketTrain extends Basket {
	/**
	 * 编号存储
	 * 
	 * @param index
	 * @param generalMushroom
	 */
	void push(int index, GeneralMushroom generalMushroom);

	/**
	 * 标识存储
	 * 
	 * @param key
	 * @param generalMushroom
	 */
	void push(String key, GeneralMushroom generalMushroom);

	GeneralMushroom poll();

	Integer[] getAliveQueueIndex();

	GeneralMushroom take(int index);
}
