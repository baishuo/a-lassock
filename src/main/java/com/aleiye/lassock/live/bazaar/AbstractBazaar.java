package com.aleiye.lassock.live.bazaar;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.google.common.base.Preconditions;

/**
 * 抽象消费
 * @author ruibing.zhao
 * @since 2015年8月26日
 * @version 2.1.2
 */
public abstract class AbstractBazaar implements Bazaar {
	private String name;
	private Basket basket;
	private LifecycleState lifecycleState;

	public AbstractBazaar() {
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket configured");
		lifecycleState = LifecycleState.START;
	}

	@Override
	public synchronized void stop() {
		lifecycleState = LifecycleState.STOP;
	}

	@Override
	public synchronized LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	@Override
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + "{name:" + name + ", basket:" + basket.getName() + "}";
	}

	@Override
	public synchronized void setBasket(Basket basket) {
		this.basket = basket;

	}

	@Override
	public synchronized Basket getBasket() {
		return this.basket;
	}

}
