package com.aleiye.lassock.live.hill.shade;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.Shade;
import com.aleiye.lassock.live.hill.ShadeRunner;

public class EventDrivenShadeRunner extends ShadeRunner {

	private LifecycleState lifecycleState;

	public EventDrivenShadeRunner() {
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		Shade source = getShade();
		source.start();
		lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		Shade source = getShade();
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
