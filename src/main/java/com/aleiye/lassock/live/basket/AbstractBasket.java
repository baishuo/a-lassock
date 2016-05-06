package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.live.NamedLifecycle;

public abstract class AbstractBasket extends NamedLifecycle implements Basket {

	public String toString() {
		return this.getClass().getName() + "{name: " + getName() + "}";
	}

}
