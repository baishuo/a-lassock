package com.aleiye.lassock.live.hill.shade;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.hill.PollableShade;
import com.aleiye.lassock.live.hill.Shade;
import com.aleiye.lassock.live.hill.ShadeRunner;
import com.aleiye.lassock.live.hill.shade.tool.ShadeExecutor;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.aleiye.lassock.live.scroll.Sign;

public class ScheduleShadeRunner extends ShadeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleShadeRunner.class);

	private LifecycleState lifecycleState;

	private TimerRunner runner;

	@Override
	public void start() {
		PollableShade source = (PollableShade) getShade();
		source.start();
		runner = new TimerRunner();
		runner.source = source;
		Sign sign = source.getSign();
		ShadeExecutor.getService()
				.scheduleAtFixedRate(runner, sign.getDelay(), sign.getPeriod(), TimeUnit.MILLISECONDS);
		lifecycleState = LifecycleState.START;
		LOGGER.info("Shade:" + source.getName() + " was running!");
	}

	@Override
	public void stop() {
		Shade shade = getShade();
		shade.stop();
		lifecycleState = LifecycleState.STOP;
		LOGGER.info("Shade:" + shade.getName() + " was stoped!");
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public class TimerRunner implements Runnable {
		private PollableShade source;

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
