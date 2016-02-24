package com.aleiye.lassock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.lassock.live.LiveContainer;
import com.aleiye.lassock.liveness.Liveness;
import com.aleiye.lassock.logging.Logging;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.DestroyableUtils;

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
			logInfo("Lassock live starting......");
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

				logInfo("Lassock live was started!");
				// 挂钩Live
				liveness.lisen(container.live());
				startupComplete.compareAndSet(false, true);
				logInfo("Lassock startup completed");
			}
		} catch (Exception e) {
			logError("Fatal error during LassockLive startup. Prepare to shutdown");
			isStartingUp.set(false);
			shutdown();
			throw e;
		}
	}

	public void shutdown() throws IOException {
		try {
			if (startupComplete.compareAndSet(true, false)) {
				isShuttingDown.set(true);
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
			logError("Fatal error during LassockLive shutdown.", e);
			isShuttingDown.set(false);
		}
	}

	public void awaitShutdown() throws InterruptedException {
		shutdownLatch.await();
	}
}
