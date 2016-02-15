package com.aleiye.raker.live.hill.shade;

import com.aleiye.raker.live.hill.Shade;
import com.aleiye.raker.live.hill.ShadeRunner;
import com.aleiye.raker.live.lifecycle.LifecycleState;

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
