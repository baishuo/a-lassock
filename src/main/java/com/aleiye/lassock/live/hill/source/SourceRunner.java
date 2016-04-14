package com.aleiye.lassock.live.hill.source;

import com.aleiye.lassock.api.Course.RunType;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.hill.executor.EventDrivenSourceRunner;
import com.aleiye.lassock.live.hill.executor.PollableSourceRunner;
import com.aleiye.lassock.live.hill.executor.QuartzGroupSourceRunner;
import com.aleiye.lassock.live.hill.executor.QuartzSourceRunner;
import com.aleiye.lassock.live.hill.executor.ScheduleSourceRunner;
import com.aleiye.lassock.live.hill.executor.TimerSourceRunner;

abstract public class SourceRunner implements LifecycleAware {
	private Source source;

	public static SourceRunner forSource(Source source) {
		SourceRunner runner = null;
		if (source instanceof EventTrackSource) {
			EventTrackSource pollable = (EventTrackSource) source;
			Sign sign = pollable.getSign();
			if (sign.getCourse().getRunType() == RunType.SCHEDULE) {
				runner = new ScheduleSourceRunner();
				((ScheduleSourceRunner) runner).setShade((EventTrackSource) source);
			} else if (sign.getCourse().getRunType() == RunType.TIMER) {
				runner = new TimerSourceRunner();
				((TimerSourceRunner) runner).setShade((EventTrackSource) source);
			} else if (sign.getCourse().getRunType() == RunType.CRON) {
				runner = new QuartzSourceRunner();
				((QuartzSourceRunner) runner).setShade((EventTrackSource) source);
			} else if (sign.getCourse().getRunType() == RunType.GROUP) {
				runner = new QuartzGroupSourceRunner();
				((QuartzGroupSourceRunner) runner).setShade((EventTrackSource) source);
			} else {
				runner = new PollableSourceRunner();
				((PollableSourceRunner) runner).setShade((EventTrackSource) source);
			}
		} else if (source instanceof EventDrivenSource) {
			runner = new EventDrivenSourceRunner();
			((EventDrivenSourceRunner) runner).setShade((EventDrivenSource) source);
		} else {
			throw new IllegalArgumentException("No known runner type for source " + source);
		}

		return runner;
	}

	public Source getShade() {
		return source;
	}

	public void setShade(Source source) {
		this.source = source;
	}

}
