package com.aleiye.lassock.live.hill.executor;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.source.EventTrackSource;
import com.aleiye.lassock.live.hill.source.Sign;
import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.SourceRunner;

/**
 * Timer
 * @author ruibing.zhao
 * @since 2015年9月29日
 */
public class TimerSourceRunner extends SourceRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerSourceRunner.class);
	private static Timer timer = new Timer("TimerShadeRunner");

	private LifecycleState lifecycleState;
	private TimerTaskRunner runner;

	@Override
	public void start() {
		EventTrackSource source = (EventTrackSource) getShade();
		source.start();
		runner = new TimerTaskRunner();
		runner.source = source;
		Sign sign = source.getSign();
		timer.schedule(runner, sign.getCourse().getDelay(), sign.getCourse().getPeriod());
		lifecycleState = LifecycleState.START;
		LOGGER.info("Shade:" + source.getName() + " was running!");
	}

	@Override
	public void stop() {
		runner.cancel();
		Source source = getShade();
		source.stop();
		lifecycleState = LifecycleState.STOP;
		LOGGER.info("Shade:" + source.getName() + " was stoped!");
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public class TimerTaskRunner extends TimerTask {
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
				LOGGER.error(source.getName() + " pick exception!");
				LOGGER.error(e.getMessage(), e);
				LOGGER.debug("", e);
			}
		}
	}
}
