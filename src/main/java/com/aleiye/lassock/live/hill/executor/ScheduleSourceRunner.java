package com.aleiye.lassock.live.hill.executor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.executor.tool.SourceExecutor;
import com.aleiye.lassock.live.hill.source.EventTrackSource;
import com.aleiye.lassock.live.hill.source.Sign;
import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.SourceRunner;

public class ScheduleSourceRunner extends SourceRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleSourceRunner.class);

	private LifecycleState lifecycleState;

	private TimerRunner runner;

	@Override
	public void start() {
		EventTrackSource source = (EventTrackSource) getShade();
		source.start();
		runner = new TimerRunner();
		runner.source = source;
		Sign sign = source.getSign();
		SourceExecutor.getService()
				.scheduleAtFixedRate(runner, sign.getCourse().getDelay(), sign.getCourse().getPeriod(), TimeUnit.MILLISECONDS);
		lifecycleState = LifecycleState.START;
		LOGGER.info("Shade:" + source.getName() + " was running!");
	}

	@Override
	public void stop() {
		Source source = getShade();
		source.stop();
		lifecycleState = LifecycleState.STOP;
		LOGGER.info("Shade:" + source.getName() + " was stoped!");
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public class TimerRunner implements Runnable {
		private EventTrackSource source;

		@Override
		public void run() {
			// 如果非开启抛出RUNTIME异常停止该任务
			if (source.getLifecycleState() != LifecycleState.START) {
				throw new IllegalStateException("Timer runner was stoped!");
			}
			try {
				source.pick();
			} catch (Exception e) {
				LOGGER.error(source.getName() + " pick exception:" + e.getMessage());
				LOGGER.debug("", e);
			}
		}
	}
}
