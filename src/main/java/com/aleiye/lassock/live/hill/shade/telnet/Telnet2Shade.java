package com.aleiye.lassock.live.hill.shade.telnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.hill.shade.AbstractPollableShade;
import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.model.MushroomBuilder;
import com.aleiye.lassock.util.ScrollUtils;

public class Telnet2Shade extends AbstractPollableShade {
	private static final Logger LOGGER = Logger.getLogger(TelnetShade.class);
	private TelnetSign sign;

	private String[] jumped;
	private String host;
	private int port;
	private String username;
	private String password;
	private String prepareCommand;
	private String[] commands;

	@Override
	public Sign getSign() {
		return this.sign;
	}

	@Override
	protected void doPick() throws Exception {
		boolean logined = false;
		ApacheTelnet telnet = new ApacheTelnet("ANSI");
		try {
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
				telnet.sendCommand("");
				if (!telnet.isSuccess()) {
					throw new Exception(host + " jump failed!");
				}
				// 继续连接跳转设备
				for (int i = 1; i < jumped.length; i++) {
					// 属性
					String comms[] = jumped[i].split(";");
					for (String s : comms) {
						telnet.sendCommand(s);
					}
					telnet.sendCommand("");
					if (!telnet.isSuccess()) {
						throw new Exception(comms[0] + " jump failed!");
					}
				}

				// 连接最终采集设备
				telnet.sendCommand(this.host);
			} else {
				// 连接尝试并获取通道.
				telnet.connect(host, port).sync();
			}

			// ------------------------------------------------------------------------
			// 登录
			// ------------------------------------------------------------------------
			if (StringUtils.isNotBlank(username)) {
				telnet.sendCommand(username);// 输入用户
			}
			if (StringUtils.isNotBlank(password)) {
				telnet.sendCommand(password);// 输入密码
			}

			if (!telnet.isSuccess()) {
				throw new Exception(this.host + "login failed!");
			}
			logined = true;
			// 执行准备命令
			if (StringUtils.isNotBlank(prepareCommand)) {
				String[] precom = prepareCommand.split(";");
				for (String s : precom) {
					telnet.sendCommand(s);
				}
			}
			if (!telnet.isSuccess()) {
				throw new Exception("Prepare command execute waring!");
			}
			// ------------------------------------------------------------------------
			// 执行命令
			List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
			for (int i = 0; i < commands.length; i++) {
				String s = telnet.sendCommandToEnd(commands[i]);
				Map<String, String> contents = new HashMap<String, String>();
				contents.put("result", s);
				contents.put("command", commands[i]);
				ret.add(contents);
			}
			// ------------------------------------------------------------------------
			apply(ret);
		} catch (Exception e) {
			LOGGER.error("Telnet " + this.host + ":" + this.port + " failed!", e);
		} finally {
			try {
				if (logined) {
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

	public void apply(List<Map<String, String>> input) {
		Mushroom generalMushroom = MushroomBuilder.withBody(input, null);
		generalMushroom.getHeaders().put("target", this.sign.getHost());
		try {
			putMushroom(sign, generalMushroom);
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage(), e);
		}
	}

	@Override
	protected void doConfigure(Course context) throws Exception {
		this.sign = (TelnetSign) ScrollUtils.forSign((Course) context, TelnetSign.class);
		// this.sign = (TelnetSign) context;
		host = sign.getHost();
		port = sign.getPort();
		username = sign.getUsername();
		password = sign.getPassword();
		prepareCommand = sign.getPrepareCommand();
		commands = sign.getCommands();
		jumped = sign.getJumped();
	}

	@Override
	protected void doStart() throws Exception {
		;
	}

	@Override
	protected void doStop() throws Exception {
		;
	}

}
