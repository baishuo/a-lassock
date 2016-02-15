package com.aleiye.raker.live.hill.shade.syslog;

import com.aleiye.raker.live.scroll.Sign;

/**
 * SYSLOG标识
 * 
 * @author ruibing.zhao
 * @since 2015年6月6日
 * @version 2.1.2
 */
public class SyslogSign extends Sign {

	// 协议 TCP,UDP
	private String protocol;
	// 服务端口号
	private int port;

	@Override
	public String getDescription() {
		return protocol + "/" + port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
