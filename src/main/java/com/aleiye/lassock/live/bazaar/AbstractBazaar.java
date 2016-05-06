package com.aleiye.lassock.live.bazaar;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.NamedLifecycle;
import com.aleiye.lassock.live.basket.Basket;
import com.google.common.base.Preconditions;

/**
 * 抽象消费
 * @author ruibing.zhao
 * @since 2015年8月26日
 * @version 2.1.2
 */
public abstract class AbstractBazaar extends NamedLifecycle implements Bazaar {
	private Basket basket;

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket configured");
		lifecycleState = LifecycleState.START;
	}

	@Override
	public synchronized void setBasket(Basket basket) {
		this.basket = basket;

	}

	@Override
	public synchronized Basket getBasket() {
		return this.basket;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "{name:" + getName() + ", basket:" + basket.getName() + "}";
	}

}
