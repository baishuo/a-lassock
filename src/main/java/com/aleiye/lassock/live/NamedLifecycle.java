package com.aleiye.lassock.live;

import com.aleiye.lassock.common.NamedComponent;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.lifecycle.LifecycleState;

/**
 * 名称和生命周期抽像
 * 
 * @author ruibing.zhao
 * @since 2016年2月18日
 * @version 1.0
 */
public abstract class NamedLifecycle implements LifecycleAware, NamedComponent {
	// 名称
	protected String name;

	@Override
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	// 状态
	protected LifecycleState lifecycleState;

	public NamedLifecycle() {
		lifecycleState = LifecycleState.IDLE;
	}

	protected synchronized void setLifecycleState(LifecycleState lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	protected boolean isStarted() {
		return getLifecycleState() == LifecycleState.START;
	}

	protected void assertStarted() {
		if (!isStarted()) {
			throw new IllegalStateException("Lifecycle was started!");
		}
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

	public String toString() {
		return this.getClass().getSimpleName() + "{name:" + name + ",state:" + lifecycleState + "}";
	}
}
