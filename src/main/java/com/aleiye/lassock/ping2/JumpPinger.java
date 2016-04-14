package com.aleiye.lassock.ping2;

import java.util.Arrays;

import com.aleiye.lassock.live.hill.source.telnet.ApacheTelnet;

public class JumpPinger extends Pinger {
	boolean conected = false;
	ApacheTelnet telnet;

	public void connect(String[] jumped) throws Exception {
		telnet = new ApacheTelnet("ANSI");
		telnet.setWaitMillis(10000);
		if (jumped != null && jumped.length > 0) {
			// 属性
			String attrs[] = jumped[0].split(";");
			// 端口
			String[] str = attrs[0].split("/");
			String host = str[0];
			int port = 23;
			if (str.length == 2) {
				port = Integer.parseInt(str[1]);
			}
			// 连接第一个跳转设备
			telnet.connect(host, port).sync();
			for (int i = 1; i < attrs.length; i++) {
				telnet.sendCommand(attrs[i]);
			}
			if (!telnet.isSuccess()) {
				throw new IllegalAccessError(host + "jump failed!");
			}
			// 继续连接跳转设备
			for (int i = 1; i < jumped.length; i++) {
				// 属性
				String comms[] = jumped[i].split(";");
				for (String s : comms) {
					telnet.sendCommand(s);
				}
				if (!telnet.isSuccess()) {
					throw new IllegalAccessError(comms[0] + "jump failed!");
				}
			}
			telnet.sendCommand("");
			if (!telnet.isSuccess()) {
				throw new IllegalAccessError("Telnet jump failed!");
			}
			conected = true;
		} else {
			throw new IllegalAccessError("jump can not be empty!");
		}
	}

	@Override
	public PingResult ping(String ip) {
		if (!conected) {
			throw new IllegalStateException("Jump not connect!");
		}
		String pingCmd = String.format(this.getPattern(), ip);
		// ping
		String s = telnet.sendCommandToEnd(pingCmd);
		return this.getParser().parse(Arrays.asList(s.split("\n")));
	}

	public void distinct() {
		try {
			if (conected == true) {
				while (true) {
					telnet.sendCommand("");
					if (telnet.isSuccess()) {
						break;
					}
				}
				while (true) {
					String s = telnet.sendCommand("exit");
					if (s.indexOf('^') > -1) {
						telnet.sendCommand("quit");
					}
					if (!telnet.isSuccess()) {
						break;
					}
				}
			}
		} catch (Exception e1) {
			;
		}
		telnet.distinct();
	}
}
