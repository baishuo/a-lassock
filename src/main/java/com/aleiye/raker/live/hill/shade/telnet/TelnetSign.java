package com.aleiye.raker.live.hill.shade.telnet;

import com.aleiye.raker.live.scroll.Sign;

/**
 * @author ruibing.zhao
 * @since 2015年8月18日
 * @version 2.1.2
 */
public class TelnetSign extends Sign {
	// 跳转
	private String jumped[];
	// 服务器地址
	private String host;
	// 端口
	private int port = 23;
	// 用户名
	private String username;
	// 密码
	private String password;
	// 准备命令
	private String prepareCommand;
	// 执行命令
	private String[] commands;

	private long waitMillis = 2000;

	@Override
	public String getDescription() {
		return username + "@" + host + ":" + port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getCommands() {
		return commands;
	}

	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	public String getPrepareCommand() {
		return prepareCommand;
	}

	public void setPrepareCommand(String prepareCommand) {
		this.prepareCommand = prepareCommand;
	}

	public long getWaitMillis() {
		return waitMillis;
	}

	public void setWaitMillis(long waitMillis) {
		this.waitMillis = waitMillis;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getJumped() {
		return jumped;
	}

	public void setJumped(String[] jumped) {
		this.jumped = jumped;
	}
}
