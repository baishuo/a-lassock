package com.aleiye.lassock.live.hill.shade;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.hill.PollableShade;
import com.aleiye.lassock.live.hill.Shade;
import com.aleiye.lassock.live.hill.ShadeRunner;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.aleiye.lassock.live.scroll.Sign;

/**
 * Timer
 * @author ruibing.zhao
 * @since 2015年9月29日
 */
public class TimerShadeRunner extends ShadeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerShadeRunner.class);
	private static Timer timer = new Timer("TimerShadeRunner");

	private LifecycleState lifecycleState;
	private TimerTaskRunner runner;

	@Override
	public void start() {
		PollableShade source = (PollableShade) getShade();
		source.start();
		runner = new TimerTaskRunner();
		runner.source = source;
		Sign sign = source.getSign();
		timer.schedule(runner, sign.getDelay(), sign.getPeriod());
		lifecycleState = LifecycleState.START;
		LOGGER.info("Shade:" + source.getName() + " was running!");
	}

	@Override
	public void stop() {
		runner.cancel();
		Shade shade = getShade();
		shade.stop();
		lifecycleState = LifecycleState.STOP;
		LOGGER.info("Shade:" + shade.getName() + " was stoped!");
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public class TimerTaskRunner extends TimerTask {
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
				LOGGER.error(source.getName() + " pick exception!");
				LOGGER.error(e.getMessage(), e);
				LOGGER.debug("", e);
			}
		}
	}
}
