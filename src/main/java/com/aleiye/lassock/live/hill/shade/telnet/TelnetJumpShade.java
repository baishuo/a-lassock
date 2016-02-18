package com.aleiye.lassock.live.hill.shade.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.hill.shade.AbstractPollableShade;
import com.aleiye.lassock.live.scroll.Course;
import com.aleiye.lassock.model.GeneralMushroom;
import com.aleiye.lassock.util.ScrollUtils;

public class TelnetJumpShade extends AbstractPollableShade {
	private static final Logger LOGGER = Logger.getLogger(TelnetJumpShade.class);

	private static final StringDecoder DECODER = new StringDecoder();
	private static final StringEncoder ENCODER = new StringEncoder();
	private static final String COMMAND_ENTER = "\r\n";
	private static final boolean SSL = System.getProperty("ssl") != null;

	private TelnetSign sign;

	private String[] jumped;
	private String host;
	private int port;
	private String username;
	private String password;
	private String prepareCommand;
	private String[] commands;
	private long millis = 2000;

	@Override
	protected void doPick() throws Exception {
		final SslContext sslCtx;
		if (SSL) {
			sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}
		EventLoopGroup group = new NioEventLoopGroup();
		Channel ch = null;
		boolean loggedin = false;
		ChannelFuture lastWriteFuture = null;
		try {
			final FutureBuffer signal = new FutureBuffer();
			Bootstrap b = new Bootstrap();
			b.group(group)
			// 异步通道
					.channel(NioSocketChannel.class)
					// 通道设置
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
					// 回值处理
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline pipeline = ch.pipeline();
							if (SSL) {
								pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
							}

							pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
							pipeline.addLast(DECODER);
							pipeline.addLast(ENCODER);
							ChannelHandler handler = new SimpleChannelInboundHandler<String>() {
								@Override
								protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
									signal.updated.compareAndSet(false, true);
									signal.buffer.append("\n");
									signal.buffer.append(msg);
									String trim = msg.trim();
									if (trim.endsWith(">") || trim.endsWith("#") || trim.endsWith("$")) {
										signal.success.set(true);
									} else
										signal.success.set(false);
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
									LOGGER.error(cause.getMessage());
									LOGGER.debug(cause.getMessage(), cause);
									ctx.close();
								}
							};
							pipeline.addLast(handler);
						}
					});
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
				ch = b.connect(host, port).sync().channel();
				Thread.sleep(millis);
				for (int i = 1; i < attrs.length; i++) {
					ch.writeAndFlush(attrs[i] + COMMAND_ENTER);
					Thread.sleep(millis);
				}
				// 继续连接跳转设备
				for (int i = 1; i < jumped.length; i++) {
					// 属性
					String comms[] = jumped[i].split(";");
					for (String s : comms) {
						ch.writeAndFlush(s + COMMAND_ENTER);
						Thread.sleep(millis);
					}
				}
				// 连接最终采集设备
				ch.writeAndFlush(this.host + COMMAND_ENTER);
				Thread.sleep(millis);
			} else {
				// 连接尝试并获取通道.
				ch = b.connect(host, port).sync().channel();
				Thread.sleep(millis);
			}
			// ------------------------------------------------------------------------
			// 登录
			// ------------------------------------------------------------------------
			ch.writeAndFlush(username + COMMAND_ENTER);// 输入用户
			Thread.sleep(millis);
			ch.writeAndFlush(password + COMMAND_ENTER);// 输入密码
			Thread.sleep(millis);
			ch.writeAndFlush(COMMAND_ENTER); // 输入一次回车
			Thread.sleep(millis);
			if (!signal.success.get()) {
				throw new Exception(this.host + "login failed!");
			}
			loggedin = true;

			// 执行准备命令
			if (StringUtils.isNotBlank(prepareCommand)) {
				String[] precom = prepareCommand.split(";");
				for (String s : precom) {
					ch.writeAndFlush(s + COMMAND_ENTER);
					Thread.sleep(millis);
				}
			}

			ch.writeAndFlush(COMMAND_ENTER);
			// 第一次非完成时等待一次
			Thread.sleep(millis);
			if (!signal.success.get()) {
				throw new Exception("Prepare command execute waring!");
			}
			// ------------------------------------------------------------------------
			// 执行命令
			List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
			for (int i = 0; i < commands.length; i++) {
				signal.buffer = new StringBuffer(); // 每执一次命令，启用新Buffer
				signal.success.set(false);
				// //执行命令
				lastWriteFuture = ch.writeAndFlush(commands[i] + COMMAND_ENTER);
				Thread.sleep(millis);
				while (true) {
					if (signal.success.get()) {
						break;
					}
					if (signal.updated.get()) {
						signal.updated.set(false);
						lastWriteFuture = ch.writeAndFlush(" ");
					} else {
						lastWriteFuture = ch.writeAndFlush(COMMAND_ENTER);
					}
					Thread.sleep(millis);
				}

				Map<String, String> contents = new HashMap<String, String>();
				contents.put("result", signal.buffer.toString());
				contents.put("command", commands[i]);
				ret.add(contents);
			}
			// ------------------------------------------------------------------------
			apply(ret);
		} catch (Exception e) {
			LOGGER.error("Telnet failed!", e);
		} finally {
			if (loggedin) {
				lastWriteFuture = ch.writeAndFlush(COMMAND_ENTER);
				Thread.sleep(millis);
				while (true) {
					lastWriteFuture = ch.writeAndFlush("exit" + COMMAND_ENTER);
					if (ch.isActive())
						lastWriteFuture = ch.writeAndFlush("quit" + COMMAND_ENTER);
					if (lastWriteFuture != null)
						lastWriteFuture.sync();
					if (!ch.isActive()) {
						break;
					}
				}

			}
			if (ch != null)
				ch.close();
			group.shutdownGracefully();
		}
	}

	public void apply(List<Map<String, String>> input) {
		GeneralMushroom generalMushroom = new GeneralMushroom();
		generalMushroom.setBody(input);
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
		millis = sign.getWaitMillis();
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

	@Override
	public Sign getSign() {
		return this.sign;
	}

	/**
	 * 异步结果缓存
	 * @author ruibing.zhao
	 * @since 2015年9月2日
	 * @version 2.1.2
	 */
	public static class FutureBuffer {
		private StringBuffer buffer = new StringBuffer();
		private AtomicBoolean updated = new AtomicBoolean();
		private AtomicBoolean success = new AtomicBoolean();
	}
}
