package com.aleiye.lassock.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ywt on 15/5/9.
 */
public class Logging {

	private Logger _LOG = null;

	protected Logger logger() {
		if (_LOG == null) {
			_LOG = LoggerFactory.getLogger(getLogName());
		}
		return _LOG;
	}

	protected String getLogName() {
		return this.getClass().getName();
	}

	public void logInfo(String msg) {
		if (logger().isInfoEnabled())
			logger().info(msg);
	}

	public void logInfo(String msg, Throwable throwable) {
		if (logger().isInfoEnabled())
			logger().info(msg, throwable);
	}

	public void logInfo(String msg, String... args) {
		if (logger().isInfoEnabled())
			logger().info(String.format(msg, args));
	}

	public void logInfo(Throwable throwable, String msg, String... args) {
		if (logger().isInfoEnabled())
			logger().info(String.format(msg, args), throwable);
	}

	public void logWarning(String msg) {
		if (logger().isWarnEnabled())
			logger().warn(msg);
	}

	public void logWarning(String msg, Throwable throwable) {
		if (logger().isWarnEnabled())
			logger().warn(msg, throwable);
	}

	public void logWarning(String msg, String... args) {
		if (logger().isWarnEnabled())
			logger().warn(String.format(msg, args));
	}

	public void logWarning(Throwable throwable, String msg, String... args) {
		if (logger().isWarnEnabled())
			logger().warn(String.format(msg, args), throwable);
	}

	public void logError(String msg) {
		if (logger().isErrorEnabled())
			logger().error(msg);
	}

	public void logError(String msg, Throwable throwable) {
		if (logger().isErrorEnabled())
			logger().error(msg, throwable);
	}

	public void logError(String msg, String... args) {
		if (logger().isErrorEnabled())
			logger().error(String.format(msg, args));
	}

	public void logError(Throwable throwable, String msg, String... args) {
		if (logger().isErrorEnabled())
			logger().error(String.format(msg, args), throwable);
	}

}
