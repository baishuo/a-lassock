package com.aleiye.lassock.live.hill.source.syslog;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.aleiye.common.exception.AuthWrongException;
import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseConst;
import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.live.hill.source.AbstractEventDrivenSource;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * SYSLOG 采集
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public class SyslogSource extends AbstractEventDrivenSource {
	private SyslogParam param;
	private InitializeAware aware;

	@Override
	protected void doConfigure(Course context) throws Exception {
		this.param = ScrollUtils.forParam(context, SyslogParam.class);
	}

	@Override
	protected void doStart() throws Exception {
		if (CourseConst.PROTOCOL_TCP.equals(param.getProtocol().toLowerCase())) {
			aware = new SyslogTCPServer(this, param.getPort());
		} else if (CourseConst.PROTOCOL_UDP.equals(param.getProtocol().toLowerCase())) {
			aware = new SyslogUDPServer(this, param.getPort());
		} else {
			throw new Exception("Syslog service start error to cannot recognize protocol:" + param.getProtocol());
		}

		aware.initialize();
	}

	@Override
	protected void doStop() throws Exception {
		aware.destroy();
	}

	public boolean isStarted() {
		return super.isStarted();
	}

	public void putMushroom(Mushroom mr) throws InterruptedException, AuthWrongException {
		super.putMushroom(mr);
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
}
