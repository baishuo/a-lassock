package com.aleiye.lassock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lassock启动程序
 * 
 * @author ruibing.zhao
 * @since 2016年2月17日
 * @version 1.0
 */
public class Lassock {

	private static Logger _LOG = LoggerFactory.getLogger(Lassock.class);

	public static void main(String[] args) {
		try {
			// Startable
			final LassockStartable startable = new LassockStartable();
			// 开启
			startable.startup();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					startable.shutdown();
				}
			});

			startable.awaitShutdown();

			System.exit(0);
		} catch (Exception e) {
			_LOG.error("Lassock startup failure!", e);
			System.exit(1);
		}
	}
}
