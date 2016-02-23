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

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.hill.shade.AbstractPollableShade;
import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.model.MushroomBuilder;
import com.aleiye.lassock.util.ScrollUtils;

public class TelnetShade extends AbstractPollableShade {
	private static final Logger LOGGER = Logger.getLogger(TelnetShade.class);
	private static final StringDecoder DECODER = new StringDecoder();
	private static final StringEncoder ENCODER = new StringEncoder();
	private static final String COMMAND_ENTER = "\r\n";
	private static final boolean SSL = System.getProperty("ssl") != null;
	private TelnetSign sign;

	private String host;
	private int port;
	private String username;
	private String password;
	private String prepareCommand;
	private String[] commands;
	private long millis = 2000;

	// public static void main(String args[]) {
	// TelnetShade shade = new TelnetShade();
	// shade.setBasket(new SimpleLogoutBasket());
	// shade.setName("test");
	// TelnetSign sign = new TelnetSign();
	// sign.setId("aaaa");
	// sign.setPrepareCommand("telnet aleiyec;pwd@123;root;pwd@123;telnet aleiyed;root;pwd@123");
	// sign.associate("1");
	// sign.setHost("10.0.1.1");
	// sign.setPort(23);
	// sign.setUesrname("admin");
	// sign.setPassword("yhxt@123");
	// String[] p = { "dis arp", "dis mac-address" };
	// sign.setCommands(p);
	// shade.configure(sign);
	// shade.start();
	// try {
	// shade.pick();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

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
			// 连接尝试并获取通道.
			ch = b.connect(host, port).sync().channel();
			Thread.sleep(millis);
			// ------------------------------------------------------------------------
			// 登录
			// ------------------------------------------------------------------------
			if (StringUtils.isNotBlank(username)) {
				ch.writeAndFlush(username + COMMAND_ENTER);// 输入用户
				Thread.sleep(millis);
			}
			if (StringUtils.isNotBlank(password)) {
				ch.writeAndFlush(password + COMMAND_ENTER);// 输入密码
				Thread.sleep(millis);
			}
			ch.writeAndFlush(COMMAND_ENTER); // 输入一次回车
			Thread.sleep(millis);
			if (!signal.success.get()) {
				throw new Exception("login failed!");
			}

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
			LOGGER.error("Telnet " + this.host + ":" + this.port + " failed!", e);
		} finally {
			if (loggedin) {
				lastWriteFuture = ch.writeAndFlush("exit" + COMMAND_ENTER);
				if (ch.isActive())
					lastWriteFuture = ch.writeAndFlush("quit" + COMMAND_ENTER);

				if (lastWriteFuture != null)
					lastWriteFuture.sync();
			}
			if (ch != null)
				ch.close();
			group.shutdownGracefully();
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
		host = sign.getHost();
		port = sign.getPort();
		username = sign.getUsername();
		password = sign.getPassword();
		prepareCommand = sign.getPrepareCommand();
		commands = sign.getCommands();
		millis = sign.getWaitMillis();
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
