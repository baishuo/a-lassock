package com.aleiye.lassock.monitor;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.IntelligenceLetter;
import com.aleiye.lassock.api.LassockInformation;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.live.NamedLifecycle;
import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.util.AkkaUtils;

/**
 * 默认监听
 * 
 * @author ruibing.zhao
 * @since 2016年2月22日
 * @version 1.0
 */
public class DefaultMonitor extends NamedLifecycle implements Monitor {
	private boolean enabled = false;
	private String host;
	private int port;
	private String systemName;

	private ActorSystem actorSystem;

	// 监控情报发送配置
	private Context target;
	// 目标
	private ActorSelection selection;

	private Timer timer;

	private Live live;

	public DefaultMonitor(Live live) {
		this.live = live;
	}

	@Override
	public void configure(Context context) {
		enabled = context.getBoolean("enabled", false);
		systemName = Sistem.getLassockname();
		host = Sistem.getHost();
		port = context.getInteger("port", Sistem.getPort());
		target = new Context(context.getSubProperties("target."));
	}

	@Override
	public synchronized void start() {
		// 是否开启监控服务
		if (enabled) {
			// 开启AKKA服务
			actorSystem = AkkaUtils.createActorSystem(host, port, systemName);
			// state 状态服务
			actorSystem.actorOf(Props.create(StatusActor.class, live), "state");
			if (target.getBoolean("enabled")) {
				timer = new Timer("timer-monitor");
				String targetHost = target.getString("host");
				int targetPort = target.getInteger("port");
				String targetName = target.getString("systemname");
				String targetRegName = target.getString("registername");
				String targetActorName = target.getString("monitorname");
				int period = target.getInteger("period", 5000);
				// 注册
				ActorSelection regSelection = actorSystem.actorSelection(AkkaUtils.getRemoteActorPath(targetHost,
						targetPort, targetName, targetRegName));
				LassockInformation info = Sistem.getInformation();
				regSelection.tell(info, ActorRef.noSender());

				// 任务监控
				selection = actorSystem.actorSelection(AkkaUtils.getRemoteActorPath(targetHost, targetPort, targetName,
						targetActorName));

				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						List<Intelligence> is = live.getIntelligences();
						LassockState state = live.getState();
						IntelligenceLetter letter = new IntelligenceLetter(state, is);
						selection.tell(letter, ActorRef.noSender());
					}
				};

				timer.schedule(tt, period, period);
			}
		}
		super.start();
	}

	@Override
	public synchronized void stop() {
		if (timer != null)
			timer.cancel();
		selection.tell(live.getState(), ActorRef.noSender());
		actorSystem.shutdown();
		super.stop();
	}

	/**
	 * 状态操作Actor
	 * 
	 * @author ruibing.zhao
	 * @since 2016年2月22日
	 * @version 1.0
	 */
	public static class StatusActor extends UntypedActor {
		LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		Live live;

		public StatusActor(Live live) {
			this.live = live;
		}

		public void onReceive(Object message) throws Exception {
			if (message instanceof Boolean) {
				if ((Boolean) message) {
					if (!live.isPaused())
						live.pause();
				} else {
					if (live.isPaused())
						live.resume();
				}
			}
			// 返回状态
			getSender().tell(live.getState(), ActorRef.noSender());
		}
	}
}
