package com.aleiye.lassock.live.hill.shade.syslog;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.model.Mushroom;

/**
 * UDP SYSLOG 采集源
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public class SyslogUDPShade extends SyslogShade {

	private final Logger LOGGER = Logger.getLogger(SyslogUDPShade.class);

	private NioEventLoopGroup bossGroup;

	public class SyslogHandler extends SimpleChannelInboundHandler<DatagramPacket> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
			if (isStarted()) {
				InetSocketAddress remoteAddress = (InetSocketAddress) packet.sender();
				ByteBuf in = packet.content();
				String ip = remoteAddress.getAddress().getHostAddress(), host = remoteAddress.getAddress()
						.getHostName();
				if (ip.equals("127.0.0.1")) {
					ip = Sistem.IP;
				}

				Mushroom mr = new Mushroom();
				ByteBuf data = in.readBytes(in.readableBytes());
				mr.setContent(data.array());
				mr.put("sender", host);
				mr.put("ip", ip);
				putMushroom(sign, mr);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doStart() throws Exception {
		LOGGER.info("Syslog UDP/" + this.sign.getPort() + " shade starting...");
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
		bossGroup = new NioEventLoopGroup(2, new NamedThreadFactory("Server-Boss"));
		Channel channel = bootstrap.group(bossGroup).channel(NioDatagramChannel.class).handler(new SyslogHandler())
				.bind(new InetSocketAddress(this.sign.getPort())).channel();
		channelGroup.add(channel);
		LOGGER.info("Syslog server UDP/" + this.sign.getPort() + " shade started!");

	}

	@Override
	protected void doStop() throws Exception {
		LOGGER.info("Syslog UDP/" + this.sign.getPort() + " stopping...");
		synchronized (this) {
			channelGroup.remove(this.serverChannel);
			if (null != bossGroup) {
				bossGroup.shutdownGracefully();
			}
		}
		LOGGER.info("Syslog UDP/" + this.sign.getPort() + " stoped!");
	}
}
