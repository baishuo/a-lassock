package com.aleiye.raker;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.raker.cache.Liveness;
import com.aleiye.raker.live.LiveContainer;
import com.aleiye.raker.logging.Logging;
import com.aleiye.raker.util.CloseableUtils;
import com.aleiye.raker.util.ConfigUtils;
import com.aleiye.raker.util.DestroyableUtils;

/**
 * 采集器
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public class LassockLive extends Logging {
	private AtomicBoolean startupComplete = new AtomicBoolean(false);
	private AtomicBoolean isShuttingDown = new AtomicBoolean(false);
	private AtomicBoolean isStartingUp = new AtomicBoolean(false);
	// 线程同步工具
	CountDownLatch shutdownLatch = new CountDownLatch(1);
	// 课程配置监听器
	private Liveness liveness;
	// 采集生涯
	private LiveContainer container;

	public void startup() throws Exception {
		try {
			logInfo("Raker live starting......");
			if (isShuttingDown.get())
				throw new IllegalStateException("Aleiye lassock is still shutting down, cannot re-start!");
			if (startupComplete.get())
				return;
			boolean canStartup = isStartingUp.compareAndSet(false, true);
			if (canStartup) {
				// 创建 liveness;
				Class<?> livenessClass = Class.forName(ConfigUtils.getConfig().getString("liveness.class"));
				liveness = (Liveness) livenessClass.newInstance();
				liveness.initialize();
				// 创建 Live
				container = new LiveContainer(ConfigUtils.getConfig());
				container.initialize();

				logInfo("Raker live was started!");

				// 挂钩Live
				liveness.lisen(container.live());
				startupComplete.compareAndSet(false, true);
				logInfo("startup completed");
			}
		} catch (Exception e) {
			logError("Fatal error during RakerLive startup. Prepare to shutdown");
			isStartingUp.set(false);
			shutdown();
			throw e;
		}
	}

	public void shutdown() throws IOException {
		try {
			if (startupComplete.compareAndSet(true, false)) {
				isShuttingDown.set(false);
				// 配置监听关闭
				CloseableUtils.closeQuietly(liveness);
				// 关闭采集生涯
				DestroyableUtils.destroyQuietly(container);

				isStartingUp.set(false);
				// 减持执行线程
				shutdownLatch.countDown();
				logInfo("shut down completed");
			} else {
				logInfo("lassock has been shutdown");
			}
		} catch (Exception e) {
			logError("Fatal error during RakerLive shutdown.", e);
			isShuttingDown.set(false);
		}
	}

	public void awaitShutdown() throws InterruptedException {
		shutdownLatch.await();
	}
}
