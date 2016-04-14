package com.aleiye.lassock.live.hill.source.syslog;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
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
 * UDP SYSLOG 采集源
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public class SyslogUDPServer implements InitializeAware {

	private final Logger LOGGER = Logger.getLogger(SyslogUDPServer.class);

	private ChannelGroup channelGroup = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);
	private Channel serverChannel;
	private NioEventLoopGroup bossGroup;

	private final int port;
	private final SyslogSource source;

	public SyslogUDPServer(SyslogSource source, int port) {
		this.source = source;
		this.port = port;
	}

	public class SyslogHandler extends SimpleChannelInboundHandler<DatagramPacket> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
			if (source.isStarted()) {
				InetSocketAddress remoteAddress = (InetSocketAddress) packet.sender();
				ByteBuf in = packet.content();
				String ip = remoteAddress.getAddress().getHostAddress(), host = remoteAddress.getAddress()
						.getHostName();
				if (ip.equals("127.0.0.1")) {
					ip = Sistem.getHost();
				}

				GeneralMushroom mr = new GeneralMushroom();
				ByteBuf data = in.readBytes(in.readableBytes());
				mr.setBody(data.array());
				mr.getHeaders().put("sender", host);
				mr.getHeaders().put("ip", ip);
				source.putMushroom(mr);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize() throws Exception {
		LOGGER.info("Syslog UDP/" + port + " shade starting...");
		Bootstrap bootstrap = new Bootstrap();
		Map<ChannelOption<?>, Object> channelOptions = new HashMap<ChannelOption<?>, Object>();
		channelOptions.put(ChannelOption.SO_SNDBUF, 65536);
		channelOptions.put(ChannelOption.SO_RCVBUF, 65536);
		channelOptions.put(ChannelOption.SO_BROADCAST, false);
		if (null != channelOptions) {
			Set<ChannelOption<?>> keySet = channelOptions.keySet();
			for (@SuppressWarnings("rawtypes")
			ChannelOption option : keySet) {
				bootstrap.option(option, channelOptions.get(option));
			}
		}
		bossGroup = new NioEventLoopGroup(2, new SyslogSource.NamedThreadFactory("Server-Boss"));
		Channel channel = bootstrap.group(bossGroup).channel(NioDatagramChannel.class).handler(new SyslogHandler())
				.bind(new InetSocketAddress(port)).channel();
		channelGroup.add(channel);
		LOGGER.info("Syslog server UDP/" + port + " shade started!");
	}

	@Override
	public void destroy() {
		LOGGER.info("Syslog UDP/" + port + " stopping...");
		channelGroup.remove(this.serverChannel);
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		channelGroup.clear();
		try {
			channelGroup.close().await();
		} catch (InterruptedException e) {
			//
		}
		LOGGER.info("Syslog UDP/" + port + " stoped!");
	}

}
