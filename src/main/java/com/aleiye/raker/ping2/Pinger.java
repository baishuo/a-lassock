package com.aleiye.raker.ping2;

/**
 * Ping
 * 
 * @author ruibing.zhao
 * @since 2015年10月13日
 */
public abstract class Pinger {
	private String pattern = "ping %s";
	private int maxSendCount = 4;
	private PingParser parser;;

	public abstract PingResult ping(String ip);

	public PingParser getParser() {
		return parser;
	}

	public void setParser(PingParser parser) {
		this.parser = parser;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getMaxSendCount() {
		return maxSendCount;
	}

	public void setMaxSendCount(int maxSendCount) {
		this.maxSendCount = maxSendCount;
	}

	/**
	 * ping 命令执行结果
	 *
	 * @author ruibing.zhao
	 * @since 2015年12月4日
	 */
	public static enum PingResult {
		SUCCESS, FAILED, LOSS
	}
}
