package com.aleiye.lassock.live.hill.source.syslog;

import com.aleiye.lassock.api.CourseConst;

/**
 * SYSLOG标识
 * 
 * @author ruibing.zhao
 * @since 2015年6月6日
 * @version 2.1.2
 */
public class SyslogParam {

	// 协议 TCP,UDP
	private String protocol = CourseConst.PROTOCOL_UDP;
	// 服务端口号
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
