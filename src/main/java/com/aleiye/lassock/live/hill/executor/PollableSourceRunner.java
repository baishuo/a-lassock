package com.aleiye.lassock.live.hill.executor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.source.EventTrackSource;
import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.SourceRunner;

public class PollableSourceRunner extends SourceRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(PollableSourceRunner.class);
	// private static final long backoffSleepIncrement = 1000;
	private static final long maxBackoffSleep = 5000;

	private AtomicBoolean shouldStop;
	private PollingRunner runner;
	private Thread runnerThread;
	private LifecycleState lifecycleState;

	public PollableSourceRunner() {
		shouldStop = new AtomicBoolean();
		lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		EventTrackSource source = (EventTrackSource) getShade();
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

		Source source = getShade();
		source.stop();
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

		private EventTrackSource source;
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
