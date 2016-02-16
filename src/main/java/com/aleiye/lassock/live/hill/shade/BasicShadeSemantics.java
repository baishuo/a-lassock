package com.aleiye.lassock.live.hill.shade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.common.able.Configurable;
import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.aleiye.lassock.live.scroll.Course;
import com.aleiye.lassock.util.LogUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public abstract class BasicShadeSemantics extends AbstractShade implements Configurable {
	private static final Logger logger = LoggerFactory.getLogger(BasicShadeSemantics.class);

	private Exception exception;

	public BasicShadeSemantics() {
		lifecycleState = LifecycleState.IDLE;
	}

	public synchronized void configure(Context context) {
		if (isStarted()) {
			throw new IllegalStateException("Configure called when started");
		} else {
			try {
				exception = null;
				setLifecycleState(LifecycleState.IDLE);
				doConfigure(context);
			} catch (Exception e) {
				exception = e;
				LogUtils.error(((Course) context).getName() + "configure exception:" + e.getMessage());
				setLifecycleState(LifecycleState.ERROR);
				Throwables.propagate(e);
			}
		}
	}

	@Override
	public synchronized void start() {
		if (exception != null) {
			logger.error(String.format("Cannot start due to error: name = %s", getName()), exception);
		} else {
			try {
				Preconditions.checkState(this.getBasket() != null, "No basket processor configured");
				doStart();
				setLifecycleState(LifecycleState.START);
			} catch (Exception e) {
				logger.error(String.format("Unexpected error performing start: name = %s", getName()), e);
				exception = e;
				setLifecycleState(LifecycleState.ERROR);
			}
		}
	}

	@Override
	public synchronized void stop() {
		try {
			doStop();
			setLifecycleState(LifecycleState.STOP);
		} catch (Exception e) {
			logger.error(String.format("Unexpected error performing stop: name = %s", getName()), e);
			setLifecycleState(LifecycleState.ERROR);
		}
	}

	protected boolean isStarted() {
		return getLifecycleState() == LifecycleState.START;
	}

	protected void assertStarted() {
		if (!isStarted()) {
			throw new IllegalStateException("Shade is not started!");
		}
	}

	protected Exception getStartException() {
		return exception;
	}

	protected synchronized void setLifecycleState(LifecycleState lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	protected abstract void doConfigure(Context context) throws Exception;

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

}
