package com.aleiye.lassock.live.hill;

import com.aleiye.lassock.api.Course.RunType;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.hill.shade.EventDrivenShadeRunner;
import com.aleiye.lassock.live.hill.shade.PollableShadeRunner;
import com.aleiye.lassock.live.hill.shade.QuartzGroupShadeRunner;
import com.aleiye.lassock.live.hill.shade.QuartzShadeRunner;
import com.aleiye.lassock.live.hill.shade.ScheduleShadeRunner;
import com.aleiye.lassock.live.hill.shade.TimerShadeRunner;

abstract public class ShadeRunner implements LifecycleAware {
	private Shade shade;

	public static ShadeRunner forSource(Shade shade) {
		ShadeRunner runner = null;
		if (shade instanceof PollableShade) {
			PollableShade pollable = (PollableShade) shade;
			Sign sign = pollable.getSign();
			if (sign.getRunType() == RunType.SCHEDULE) {
				runner = new ScheduleShadeRunner();
				((ScheduleShadeRunner) runner).setShade((PollableShade) shade);
			} else if (sign.getRunType() == RunType.TIMER) {
				runner = new TimerShadeRunner();
				((TimerShadeRunner) runner).setShade((PollableShade) shade);
			} else if (sign.getRunType() == RunType.CRON) {
				runner = new QuartzShadeRunner();
				((QuartzShadeRunner) runner).setShade((PollableShade) shade);
			} else if (sign.getRunType() == RunType.GROUP) {
				runner = new QuartzGroupShadeRunner();
				((QuartzGroupShadeRunner) runner).setShade((PollableShade) shade);
			} else {
				runner = new PollableShadeRunner();
				((PollableShadeRunner) runner).setShade((PollableShade) shade);
			}
		} else if (shade instanceof EventDrivenShade) {
			runner = new EventDrivenShadeRunner();
			((EventDrivenShadeRunner) runner).setShade((EventDrivenShade) shade);
		} else {
			throw new IllegalArgumentException("No known runner type for source " + shade);
		}

		return runner;
	}

	public Shade getShade() {
		return shade;
	}

	public void setShade(Shade shade) {
		this.shade = shade;
	}

}
