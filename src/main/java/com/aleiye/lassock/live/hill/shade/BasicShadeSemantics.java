package com.aleiye.lassock.live.hill.shade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.Intelligence.ShadeState;
import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.util.LogUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public abstract class BasicShadeSemantics extends AbstractShade {
	private static final Logger logger = LoggerFactory.getLogger(BasicShadeSemantics.class);

	private Exception exception = null;

	public synchronized void configure(Course course) {
		if (isStarted()) {
			throw new IllegalStateException("Configure called when started");
		}
		try {
			setLifecycleState(LifecycleState.IDLE);
			this.intelligence = new Intelligence(this.name);
			doConfigure(course);
			this.intelligence.setType(course.getType().toString());
		} catch (Exception e) {
			this.intelligence.setState(ShadeState.ERROR);
			exception = e;
			LogUtils.error(course.getName() + "configure exception:" + e.getMessage());
			setLifecycleState(LifecycleState.ERROR);
			Throwables.propagate(e);
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
				LogUtils.error(this.getName() + "start exception:" + e.getMessage());
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

	protected Exception getStartException() {
		return exception;
	}

	protected abstract void doConfigure(Course course) throws Exception;

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

}
