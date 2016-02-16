package com.aleiye.lassock.live.hill.shade.syslog;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.live.hill.shade.AbstractEventDrivenShade;
import com.aleiye.lassock.live.scroll.Sign;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * SYSLOG 采集子源
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public abstract class SyslogShade extends AbstractEventDrivenShade {

	protected final static ChannelGroup channelGroup = new DefaultChannelGroup("NADRON-CHANNELS",
			GlobalEventExecutor.INSTANCE);

	protected SyslogSign sign;

	protected Channel serverChannel;

	@Override
	protected void doConfigure(Context context) throws Exception {
		this.sign = (SyslogSign) ScrollUtils.forSign(context, SyslogSign.class);
	}

	public static class NamedThreadFactory implements ThreadFactory {
		private static AtomicInteger counter = new AtomicInteger(1);
		private String name = "Lane";

		private boolean daemon;
		private int priority;

		public NamedThreadFactory(String name) {
			this(name, false, -1);
		}

		public NamedThreadFactory(String name, boolean daemon) {
			this(name, daemon, -1);
		}

		public NamedThreadFactory(String name, boolean daemon, int priority) {
			this.name = name;
			this.daemon = daemon;
			this.priority = priority;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, name + "[" + counter.getAndIncrement() + "]");
			thread.setDaemon(daemon);
			if (priority != -1) {
				thread.setPriority(priority);
			}
			return thread;
		}
	}

	@Override
	public Sign getSign() {
		return this.sign;
	}
}
