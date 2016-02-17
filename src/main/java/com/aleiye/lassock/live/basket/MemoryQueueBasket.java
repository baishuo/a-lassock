package com.aleiye.lassock.live.basket;

import java.util.concurrent.LinkedBlockingQueue;

import com.aleiye.lassock.model.GeneralMushroom;

/**
 * 内存队列基础型Basket
 * @author ruibing.zhao
 * @since 2015年5月27日
 * @version 2.1.2
 */
public class MemoryQueueBasket extends AbstractBasket {
	LinkedBlockingQueue<GeneralMushroom> queue = new LinkedBlockingQueue<GeneralMushroom>();

	@Override
	public void push(GeneralMushroom generalMushroom) throws InterruptedException {
		queue.put(generalMushroom);
	}

	@Override
	public GeneralMushroom take() throws InterruptedException {
		return queue.take();
	}
}
