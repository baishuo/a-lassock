package com.aleiye.raker.live.basket;

import java.util.concurrent.LinkedBlockingQueue;

import com.aleiye.raker.model.Mushroom;

/**
 * 内存队列基础型Basket
 * @author ruibing.zhao
 * @since 2015年5月27日
 * @version 2.1.2
 */
public class MemoryQueueBasket extends AbstractBasket {
	LinkedBlockingQueue<Mushroom> queue = new LinkedBlockingQueue<Mushroom>();

	@Override
	public void push(Mushroom mushroom) throws InterruptedException {
		queue.put(mushroom);
	}

	@Override
	public Mushroom take() throws InterruptedException {
		return queue.take();
	}
}
