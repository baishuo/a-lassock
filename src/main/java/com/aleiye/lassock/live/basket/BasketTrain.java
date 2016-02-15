package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.model.Mushroom;

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
	 * @param mushroom
	 */
	void push(int index, Mushroom mushroom);

	/**
	 * 标识存储
	 * 
	 * @param key
	 * @param mushroom
	 */
	void push(String key, Mushroom mushroom);

	Mushroom poll();

	Integer[] getAliveQueueIndex();

	Mushroom take(int index);
}
