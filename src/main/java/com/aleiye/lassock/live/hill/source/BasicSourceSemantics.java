package com.aleiye.lassock.live.hill.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence.ShadeState;
import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.util.LogUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * 基础采集
 * 
 * @author ruibing.zhao
 * @since 2016年4月14日
 * @version 1.0
 */
public abstract class BasicSourceSemantics extends AbstractSource {
	private static final Logger logger = LoggerFactory.getLogger(BasicSourceSemantics.class);

	@Override
	public synchronized void configure(Course course) {
		if (isStarted()) {
			throw new IllegalStateException("Configure called when started");
		}
		this.sign = new Sign(course);
		try {
			setLifecycleState(LifecycleState.IDLE);
			doConfigure(course);
		} catch (Exception e) {
			this.sign.getIntelligence().setState(ShadeState.ERROR);
			this.sign.setException(e);
			LogUtils.error(this.getName() + "configure exception:" + e.getMessage());
			setLifecycleState(LifecycleState.ERROR);
			Throwables.propagate(e);
		}
	}

	@Override
	public synchronized void start() {
		if (this.sign.getException() != null) {
			logger.error(String.format("Cannot start due to error: name = %s", getName()), this.sign.getException());
		} else {
			try {
				Preconditions.checkState(this.getBasket() != null, "No basket processor configured");
				doStart();
				super.start();
			} catch (Exception e) {
				logger.error(String.format("Unexpected error performing start: name = %s", getName()), e);
				this.sign.setException(e);
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

	protected abstract void doConfigure(Course course) throws Exception;

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

}
