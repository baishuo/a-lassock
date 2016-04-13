package com.aleiye.lassock.lifecycle;

/**
 * 抽象生命周期
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public abstract class AbstractLifecycleAware implements LifecycleAware {
	// 状态
	protected LifecycleState lifecycleState;

	public AbstractLifecycleAware() {
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

}
