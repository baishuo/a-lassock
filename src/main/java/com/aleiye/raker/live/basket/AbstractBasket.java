package com.aleiye.raker.live.basket;

import com.aleiye.raker.common.Context;
import com.aleiye.raker.live.conf.Configurable;
import com.aleiye.raker.live.lifecycle.LifecycleState;

public abstract class AbstractBasket implements Basket, Configurable {

	private String name;

	private LifecycleState lifecycleState;

	public AbstractBasket() {
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public synchronized void start() {
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
	public synchronized String getName() {
		return name;
	}

	@Override
	public void configure(Context context) {

	}

	public String toString() {
		return this.getClass().getName() + "{name: " + name + "}";
	}

}
