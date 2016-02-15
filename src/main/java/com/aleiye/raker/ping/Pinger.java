package com.aleiye.raker.ping;

/**
 * Ping
 * 
 * @author ruibing.zhao
 * @since 2015年10月13日
 */
public abstract class Pinger {

	private PingParser parser = new DefaultPingParser();
	private int maxPingCount = 4;

	public abstract int ping(String ip, int count);

	public int ping(String ip) {
		return ping(ip, maxPingCount);
	}

	public PingParser getParser() {
		return parser;
	}

	public void setParser(PingParser parser) {
		this.parser = parser;
	}

	public int getMaxPingCount() {
		return maxPingCount;
	}
}
