package com.aleiye.lassock;

import com.aleiye.lassock.logging.Logging;

/**
 * 彩集器 启动程序
 * 
 * @author ruibing.zhao
 * @since 2015年4月17日
 * @version 2.1.2
 */
public class LassockStartable extends Logging {
	private LassockLive live;

	public LassockStartable() throws Exception {
		live = new LassockLive();
	}

	public void startup() {
		try {
			live.startup();
		} catch (Exception e) {
			logError("Fatal error during LassockStartable startup. Prepare to shutdown", e);
			System.exit(1);
		}
	}

	public void shutdown() {
		try {
			live.shutdown();
		} catch (Exception e) {
			logError("Fatal error during LassockStartable shutdown. Prepare to halt", e);
			System.exit(1);
		}
	}

	public void awaitShutdown() throws InterruptedException {
		live.awaitShutdown();
	}
}
