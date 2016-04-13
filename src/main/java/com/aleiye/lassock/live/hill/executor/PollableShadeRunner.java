package com.aleiye.lassock.live.hill.executor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.shade.PollableShade;
import com.aleiye.lassock.live.hill.shade.Shade;
import com.aleiye.lassock.live.hill.shade.ShadeRunner;

public class PollableShadeRunner extends ShadeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(PollableShadeRunner.class);
	// private static final long backoffSleepIncrement = 1000;
	private static final long maxBackoffSleep = 5000;

	private AtomicBoolean shouldStop;
	private PollingRunner runner;
	private Thread runnerThread;
	private LifecycleState lifecycleState;

	public PollableShadeRunner() {
		shouldStop = new AtomicBoolean();
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		PollableShade source = (PollableShade) getShade();
		source.start();
		runner = new PollingRunner();
		runner.shouldStop = shouldStop;
		runnerThread = new Thread(runner);
		runnerThread.setName(getClass().getSimpleName() + "-" + source.getClass().getSimpleName() + "-"
				+ source.getName());
		runnerThread.start();
		lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		runner.shouldStop.set(true);
		try {
			runnerThread.interrupt();
			runnerThread.join();
		} catch (InterruptedException e) {
			LOGGER.warn("Interrupted while waiting for polling runner to stop. Please report this.", e);
			Thread.currentThread().interrupt();
		}

		Shade shade = getShade();
		shade.stop();
		lifecycleState = LifecycleState.STOP;
	}

	@Override
	public String toString() {
		return "PollableShadeRunner: { shade:" + getShade() + " }";
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public static class PollingRunner implements Runnable {

		private PollableShade source;
		private AtomicBoolean shouldStop;

		@Override
		public void run() {
			LOGGER.debug("Polling runner starting. Shade:{}", source);
			while (!shouldStop.get()) {
				try {
					source.pick();
				} catch (Exception e) {
					LOGGER.error(source.getName() + " pick exception!");
					LOGGER.error("Unhandled exception, logging and sleeping for " + maxBackoffSleep + "ms", e);
					try {
						Thread.sleep(maxBackoffSleep);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
			LOGGER.debug("Polling runner exiting. Shade:{}", source);
		}
	}

}
