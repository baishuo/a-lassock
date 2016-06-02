package com.aleiye.lassock.live.bazaar;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.lifecycle.LifecycleState;

/**
 * <p>
 * A driver for {@linkplain Sink sinks} that polls them, attempting to
 * {@linkplain Sink#process() process} events if any are available in the
 * {@link Channel}.
 * </p>
 *
 * <p>
 * Note that, unlike {@linkplain Source sources}, all sinks are polled.
 * </p>
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SourceRunner
 */
public class BazaarRunner implements LifecycleAware {

	private static final Logger logger = LoggerFactory.getLogger(BazaarRunner.class);

	private PollingRunner runner;
	private Thread runnerThread;
	private LifecycleState lifecycleState;

	private Bazaar policy;

	public static BazaarRunner forBazaar(Bazaar bazaar) {
		BazaarRunner br = new BazaarRunner(bazaar);
		return br;
	}

	public BazaarRunner() {
		lifecycleState = LifecycleState.IDLE;
	}

	public BazaarRunner(Bazaar policy) {
		this();
		setBazaar(policy);
	}

	public Bazaar getBazaar() {
		return policy;
	}

	public void setBazaar(Bazaar policy) {
		this.policy = policy;
	}

	@Override
	public void start() {
		Bazaar policy = getBazaar();
		policy.start();
		runner = new PollingRunner();

		runner.policy = policy;
		runner.shouldStop = new AtomicBoolean();

		runnerThread = new Thread(runner);
		runnerThread.setName("SinkRunner-PollingRunner-" + policy.getClass().getSimpleName() + ":" + policy.getName());
		runnerThread.start();
		lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		if (runnerThread != null) {
			runner.shouldStop.set(true);
			runnerThread.interrupt();

			while (runnerThread.isAlive()) {
				try {
					logger.debug("Waiting for runner thread to exit");
					runnerThread.join(500);
				} catch (InterruptedException e) {
					logger.debug("Interrupted while waiting for runner thread to exit. Exception follows.", e);
				}
			}
		}
		getBazaar().stop();
		lifecycleState = LifecycleState.STOP;
	}

	@Override
	public String toString() {
		return "SinkRunner: { policy:" + getBazaar() + " }";
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public static class PollingRunner implements Runnable {

		private Bazaar policy;
		private AtomicBoolean shouldStop;

		@Override
		public void run() {
			logger.debug("Polling bazaar runner starting");
			while (!shouldStop.get()) {
				try {
					policy.process();
				} catch (Exception e) {
					logger.error("Unable to deliver event. Exception follows.", e);
				}
			}
			logger.debug("Polling bazaar runner exiting. Metrics:{}", policy.getClass().getSimpleName());
		}

	}
}
