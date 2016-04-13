package com.aleiye.lassock.liveness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.conf.Context;
import com.aleiye.lassock.lifecycle.AbstractLifecycleAware;
import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.util.LogUtils;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;

public abstract class AbstractLiveness extends AbstractLifecycleAware implements Liveness {
	private static final Logger logger = LoggerFactory.getLogger(AbstractLiveness.class);

	protected EventBus eventBus;

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void ExceptionHandler(Throwable exception, SubscriberExceptionContext context) {
		;
	}

	@Override
	public void configure(Context context) {
		try {
			setLifecycleState(LifecycleState.IDLE);
			doConfigure(context);
		} catch (Exception e) {
			setLifecycleState(LifecycleState.ERROR);
			LogUtils.error("Liveness configure exception:" + e.getMessage());
			Throwables.propagate(e);
		}
	}

	@Override
	public synchronized void start() {
		try {
			doStart();
			super.start();
		} catch (Exception e) {
			setLifecycleState(LifecycleState.ERROR);
			logger.error("Unexpected error performing start!", e);
			Throwables.propagate(e);
		}

	}

	@Override
	public synchronized void stop() {
		try {
			doStop();
		} catch (Exception e) {
			setLifecycleState(LifecycleState.ERROR);
			logger.error("Unexpected error performing stop!", e);
		}
		super.stop();
	}

	protected abstract void doConfigure(Context context) throws Exception;

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

}
