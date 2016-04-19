package com.aleiye.lassock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.conf.LivenessConfiguration;
import com.aleiye.lassock.live.LiveContainer;
import com.aleiye.lassock.liveness.Liveness;
import com.aleiye.lassock.monitor.DefaultMonitor;
import com.aleiye.lassock.monitor.Monitor;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.DestroyableUtils;

/**
 * 采集器
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public class LassockLive {
	private static final Logger logger = LoggerFactory.getLogger(LassockLive.class);

	private AtomicBoolean startupComplete = new AtomicBoolean(false);
	private AtomicBoolean isShuttingDown = new AtomicBoolean(false);
	private AtomicBoolean isStartingUp = new AtomicBoolean(false);
	// 线程同步工具
	private CountDownLatch shutdownLatch = new CountDownLatch(1);
	// 课程配置监听器
	private Liveness liveness;
	// 采集生涯
	private LiveContainer container;
	// 监控器
	private Monitor monitor;

	public void startup() throws Exception {
		try {
			logger.info("Lassock starting......");
			if (isShuttingDown.get())
				throw new IllegalStateException("Lassock is still shutting down, cannot re-start!");
			if (startupComplete.get())
				return;
			boolean canStartup = isStartingUp.compareAndSet(false, true);
			if (canStartup) {
				// Live 初始化
				logger.info("Initializing live!");
				container = new LiveContainer(ConfigUtils.getConfig());
				container.initialize();
				logger.info("Live was initialized!");

				// liveness初始化;
				Context livenessContext = ConfigUtils.getContext("liveness");
				LivenessConfiguration lc = new LivenessConfiguration(livenessContext, container.live());
				liveness = lc.getInstance();
				liveness.start();

				// 监控
				monitor = new DefaultMonitor(container.live());
				monitor.configure(ConfigUtils.getContext("monitor"));
				monitor.start();

				startupComplete.compareAndSet(false, true);
				logger.info("Lassock startup completed");
			}
		} catch (Exception e) {
			logger.error("Fatal error during LassockLive startup. Prepare to shutdown");
			shutdown();
			isStartingUp.set(false);
			throw e;
		}
	}

	public void shutdown() throws Exception {
		if (startupComplete.compareAndSet(true, false)) {
			// 配置监听关闭
			liveness.stop();
			// 关闭监控
			monitor.stop();
			// 关闭采集生涯
			DestroyableUtils.destroyQuietly(container);
			isStartingUp.set(false);
			// 减持执行线程
			shutdownLatch.countDown();
			logger.info("Lassock shutdown completed");
		} else {
			logger.info("Lassock has been shutdown");
		}
		isShuttingDown.set(true);
	}

	public void awaitShutdown() throws InterruptedException {
		shutdownLatch.await();
	}
}
