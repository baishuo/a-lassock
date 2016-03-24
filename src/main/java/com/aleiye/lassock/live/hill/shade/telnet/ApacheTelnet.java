package com.aleiye.lassock.live.hill.shade.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;

/**
 * Telnet操作器,基于commons-net
 * 
 * @author ruibing.zhao
 * @since 2015年10月8日
 */
public class ApacheTelnet {
	private static final Logger LOGGER = Logger.getLogger(ApacheTelnet.class);
	private static final int TIME_OUT = 10000;
	private TelnetClient telnet; // 客户端
	private InputStream in; // 输入流,接收返回信息
	private PrintStream out; // 向服务器写入 命令
	private Thread echo; // 回显线程
	private long waitMillis = 3000;

	// 命令 是否执行完毕
	private AtomicBoolean success = new AtomicBoolean();
	Object lock = new Object();

	private StringBuffer buf = new StringBuffer();
	private StringBuffer real = new StringBuffer();
	private StringBuffer segment = new StringBuffer();

	/**
	 * @param termtype
	 *            协议类型：VT100、VT52、VT220、VTNT、ANSI
	 */
	public ApacheTelnet(String termtype) {
		telnet = new TelnetClient(termtype);
		telnet.setConnectTimeout(TIME_OUT);
	}

	public ApacheTelnet() {
		telnet = new TelnetClient();
		telnet.setConnectTimeout(TIME_OUT);
	}

	/**
	 * 登录到目标主机
	 * 
	 * @param ip
	 * @param port
	 * @throws Exception
	 */
	public ApacheTelnet connect(String ip, int port) throws Exception {
		try {
			telnet.connect(ip, port);
		} catch (Exception e) {
			throw new Exception("Connect " + ip + ":" + port + "failed!", e);
		}
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());
		echo = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					char ch = 0;
					int code = -1;
					while ((code = in.read()) != -1) {
						ch = (char) code;
						buf.append(ch);
						real.append(ch);

//						 System.out.print(ch);

						if (in.available() == 0) {
							String str = real.toString().trim();
							if (StringUtils.isNotBlank(str)) {
								char lc = str.charAt(str.length() - 1);
								if (lc == '>' || lc == '#' || lc == '$') {
									success.set(true);
									synchronized (lock) {
										segment.setLength(0);
										segment.append(real);
										real.setLength(0);
										lock.notify();
									}
									continue;
								}
							}
							if (str.endsWith("...") || str.endsWith("... Open")) {
								Thread.sleep(2000);
							} else if (str.endsWith("re--") || str.endsWith("re --") || str.endsWith("re ----")) {

							} else {
								Thread.sleep(1000);
							}
							if (in.available() == 0) {
								synchronized (lock) {
									segment.setLength(0);
									segment.append(real);
									real.setLength(0);
									lock.notify();
								}
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e);
					}
				}
			}
		});
		echo.start();
		return this;
	}

	public void sync() throws InterruptedException {
		synchronized (lock) {
			lock.wait(waitMillis);
		}
	}

	public String getPrew() {
		return segment.toString();
	}

	public boolean isSuccess() {
		return this.success.get();
	}

	private ApacheTelnet writeln(String command) {
		success.set(false);
		out.println(command);
		out.flush();
		return this;
	}

	private ApacheTelnet write(String command) {
		success.set(false);
		out.print(command);
		out.flush();
		return this;
	}

	/**
	 * 发送命令,返回执行结果
	 * 
	 * @param command
	 * @return
	 */
	public String sendCommand(String command) {
		try {
			writeln(command).sync();
		} catch (InterruptedException e) {
			;
		}
		return this.segment.toString();
	}

	/**
	 * 发送命令,返回执行结果
	 * 
	 * @param command
	 * @return
	 */
	public String sendCommandToEnd(String command) {
		StringBuffer r = new StringBuffer();
		try {
			writeln(command).sync();
			r.append(this.segment.toString());
			while (!success.get()) {
				try {
					write(" ").sync();
				} catch (InterruptedException e) {
					writeln("").sync();
				}
				r.append(this.segment.toString());
			}
		} catch (InterruptedException e1) {
			r.append(this.segment);
		}
		return r.toString();
	}

	/**
	 * 关闭连接
	 */
	public void distinct() {
		try {
			if (telnet != null && telnet.isConnected())
				telnet.disconnect();
			if (echo != null) {
				echo.interrupt();
			}
		} catch (IOException e) {
			LOGGER.error("Distinct telnet has exception!", e);
		}
	}

	public long getWaitMillis() {
		return waitMillis;
	}

	public void setWaitMillis(long waitMillis) {
		this.waitMillis = waitMillis;
	}
}
