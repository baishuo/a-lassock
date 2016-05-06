package com.aleiye.lassock.live.hill.source.text;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.aleiye.lassock.live.hill.source.text.cluser.CluserState;
import com.aleiye.lassock.live.hill.source.text.cluser.TextCluser;
import com.aleiye.lassock.util.ConfigUtils;
import com.typesafe.config.Config;

/**
 * 采集驱动器
 * 
 * @author ruibing.zhao
 * @since 2015年5月20日
 * @version 2.2.1
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
	private static final Config config = ConfigUtils.getConfig().getConfig("executor");
	private static final int POOL_SIZE = config.getInt("size");
	private static final int MAX_SIZE = config.getInt("maxSize");
	private static final long KEEP_ALIVE_TIME = config.getLong("keepAliveTime");
	private static final int QUEUE_SIZE = config.getInt("queueSize");

	// 暂停开关
	protected AtomicBoolean stoped = new AtomicBoolean(false);
	// 暂停开关
	protected AtomicBoolean paused = new AtomicBoolean(false);
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();

	private static CustomThreadPoolExecutor executor;

	public synchronized static CustomThreadPoolExecutor newExecutor() {
		if (executor == null) {
			// 初始化执行线程
			BlockingQueue<Runnable> exeQeue;
			if (QUEUE_SIZE == 0) {
				exeQeue = new LinkedBlockingQueue<Runnable>();
			} else {
				exeQeue = new LinkedBlockingQueue<Runnable>(QUEUE_SIZE);
			}
			// if (StringUtils.isBlank(REJECTED)) {
			// return new CustomThreadPoolExecutor(POOL_SIZE, MAX_SIZE,
			// KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, exeQeue);
			// } else {
			// RejectedExecutionHandler handler =
			// ClassUtils.<RejectedExecutionHandler> newInstance(REJECTED);
			// return new CustomThreadPoolExecutor(POOL_SIZE, MAX_SIZE,
			// KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, exeQeue,
			// handler);
			// }
			executor = new CustomThreadPoolExecutor(POOL_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
					exeQeue, new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return executor;
	}

	private CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		if (stoped.get()) {
			TextCluser shade = (TextCluser) r;
			shade.returnQueue();
			return;
		}
		pauseLock.lock();
		try {
			while (paused.get())
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		TextCluser shade = (TextCluser) r;
		// 正常时放入执行队列继续读
		if (shade.getState() == CluserState.NORMAL) {
			execute(r);
		} else {
			shade.returnQueue();
		}
	}

	// 暂停
	public void pause() {
		pauseLock.lock();
		try {
			paused.compareAndSet(false, true);
		} finally {
			pauseLock.unlock();
		}
	}

	public boolean isPaused() {
		return paused.get();
	}

	// 恢复
	public void resume() {
		pauseLock.lock();
		try {
			paused.compareAndSet(true, false);
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

	// 关闭执行
	public void stoped() {
		stoped.compareAndSet(false, true);
	}

	/**
	 * 线程池抛弃策略，等待
	 * 
	 * @author ruibing.zhao
	 * @since 2015年5月18日
	 * @version 2.1.2
	 */
	public class RejectedPolicy implements RejectedExecutionHandler {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			if (!executor.isShutdown()) {
				TextCluser shade = (TextCluser) r;
				shade.returnQueue();
			}
		}
	}
}
