package com.aleiye.lassock.live.hill.executor;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.SourceRunner;

public class EventDrivenSourceRunner extends SourceRunner {

	private LifecycleState lifecycleState;

	public EventDrivenSourceRunner() {
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		Source source = getShade();
		source.start();
		lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		Source source = getShade();
		source.stop();
		lifecycleState = LifecycleState.STOP;
	}

	@Override
	public String toString() {
		return "EventDrivenShadeRunner: { shade:" + getShade() + " }";
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

}
