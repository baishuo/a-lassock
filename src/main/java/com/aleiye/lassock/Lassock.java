package com.aleiye.lassock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Raker(搂耙)的入口程序
 * Created by ywt on 15/5/9.
 */
public class Lassock {

	private static Logger _LOG = LoggerFactory.getLogger(Lassock.class);

	public static void main(String[] args) {

		_LOG.info("Running Lassock");
		try {
			// 初始System值
//			Env.load(ConfigUtils.getConfig().getConfig("system"));
			// live
			final LassockStartable startable = new LassockStartable();
			// 开启
			startable.startup();

			_LOG.info("Raker was Lassock!");

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					startable.shutdown();
				}
			});

			startable.awaitShutdown();

			System.exit(0);
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			_LOG.error("Lassock startup failure!");
			System.exit(1);
		}
	}
}
