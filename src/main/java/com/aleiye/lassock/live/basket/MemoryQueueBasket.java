package com.aleiye.lassock.live.basket;

import java.util.concurrent.LinkedBlockingQueue;

import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.model.Mushroom;

/**
 * 内存队列基础型Basket
 * @author ruibing.zhao
 * @since 2015年5月27日
 * @version 2.1.2
 */
public class MemoryQueueBasket extends AbstractBasket {
	LinkedBlockingQueue<Mushroom> queue = new LinkedBlockingQueue<Mushroom>();

	@Override
	public void push(Mushroom generalMushroom) throws InterruptedException {
		queue.put(generalMushroom);
	}

	@Override
	public Mushroom take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub

	}
}
