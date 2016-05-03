package com.aleiye.lassock.live.hill.source.syslog;

import com.aleiye.event.constants.EventKey;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.model.GeneralMushroom;

/**
 * Syslog TCP采集器
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.1
 */
public class SyslogTCPServer implements InitializeAware {
	private static final Logger LOGGER = Logger.getLogger(SyslogTCPServer.class);

	private ChannelGroup channelGroup = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);
	private Channel serverChannel;
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;

	private final int port;
	private final SyslogSource source;

	public SyslogTCPServer(SyslogSource source, int port) {
		this.source = source;
		this.port = port;
	}

	public class SyslogTcpHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (source.isStarted()) {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
				ByteBuf in = (ByteBuf) msg;
				ByteBuf data = in.readBytes(in.readableBytes());
				String ip = address.getAddress().getHostAddress();
				if (ip.equals("127.0.0.1")) {
					ip = Sistem.getIp();
				}
				GeneralMushroom mr = new GeneralMushroom();
				mr.setBody(data.array());
				mr.getHeaders().put(EventKey.REMOTENAME, address.getAddress().getHostName());
				mr.getHeaders().put(EventKey.REMOTEIP, ip);
				source.putMushroom(mr);
			}
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			LOGGER.warn("Unexpected exception from downstream.", cause);
			ctx.close();
		}
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	@Override
	public void initialize() throws Exception {
		LOGGER.info("Syslog TCP/" + this.port + " shade starting...");
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		Map<ChannelOption<?>, Object> channelOptions = new HashMap<ChannelOption<?>, Object>();
		channelOptions.put(ChannelOption.SO_KEEPALIVE, true);
		channelOptions.put(ChannelOption.SO_BACKLOG, 100);
		if (null != channelOptions) {
			Set<ChannelOption<?>> keySet = channelOptions.keySet();
			for (ChannelOption option : keySet) {
				serverBootstrap.option(option, channelOptions.get(option));
			}
		}
		bossGroup = new NioEventLoopGroup(2, new SyslogSource.NamedThreadFactory("Server-Boss"));
		workerGroup = new NioEventLoopGroup(2, new SyslogSource.NamedThreadFactory("Server-Worker"));
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new SyslogTcpHandler());
					}
				});
		serverChannel = serverBootstrap.bind(new InetSocketAddress(port)).sync().channel();
		channelGroup.add(serverChannel);
		LOGGER.info("Syslog TCP/" + this.port + " shade started");
	}

	@Override
	public void destroy() {
		LOGGER.info("Syslog TCP/" + this.port + " shade stopping...");
		channelGroup.remove(this.serverChannel);
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
		channelGroup.clear();
		channelGroup.clear();
		LOGGER.info("Syslog TCP/" + this.port + " shade stoped!");
	}
}
