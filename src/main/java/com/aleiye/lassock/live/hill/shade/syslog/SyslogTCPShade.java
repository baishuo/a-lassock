package com.aleiye.lassock.live.hill.shade.syslog;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.model.GeneralMushroom;

/**
 * Syslog TCP采集器
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.1
 */
public class SyslogTCPShade extends SyslogShade {
	private static final Logger LOGGER = Logger.getLogger(SyslogTCPShade.class);

	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;

	public class SyslogTcpHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (isStarted()) {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
				ByteBuf in = (ByteBuf) msg;
				ByteBuf data = in.readBytes(in.readableBytes());
				String ip = address.getAddress().getHostAddress();
				if (ip.equals("127.0.0.1")) {
					ip = Sistem.getIp();
				}
				GeneralMushroom mr = new GeneralMushroom();
				mr.setBody(data.array());
				mr.getHeaders().put("sender", address.getAddress().getHostName());
				mr.getHeaders().put("ip", ip);
				putMushroom(sign, mr);
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
	protected void doStart() throws Exception {
		LOGGER.info("Syslog TCP/" + this.sign.getPort() + " shade starting...");
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
		bossGroup = new NioEventLoopGroup(2, new NamedThreadFactory("Server-Boss"));
		workerGroup = new NioEventLoopGroup(2, new NamedThreadFactory("Server-Worker"));
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new SyslogTcpHandler());
					}
				});
		serverChannel = serverBootstrap.bind(new InetSocketAddress(sign.getPort())).sync().channel();
		channelGroup.add(serverChannel);
		LOGGER.info("Syslog TCP/" + this.sign.getPort() + " shade started");

	}

	@Override
	protected void doStop() throws Exception {
		LOGGER.info("Syslog TCP/" + this.sign.getPort() + " shade stopping...");
		channelGroup.remove(this.serverChannel);
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
		LOGGER.info("Syslog TCP/" + this.sign.getPort() + " shade stoped!");
	}
}
