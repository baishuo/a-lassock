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

import akka.pattern.Patterns;
import akka.util.Timeout;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.api.IntelligenceLetter;
import com.aleiye.lassock.api.LassockInformation;
import com.aleiye.lassock.api.LassockState;
import com.aleiye.lassock.api.LassockState.RunState;
import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.lifecycle.AbstractLifecycleAware;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.MixedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * 默认监听
 *
 * @author ruibing.zhao
 * @version 1.0
 * @since 2016年2月22日
 */
public class DefaultMonitor extends AbstractLifecycleAware implements Monitor {

    Logger _LOG = LoggerFactory.getLogger(DefaultMonitor.class);

    private boolean enabled = false;
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
        target = new Context(context.getSubProperties("target."));
    }

    @Override
    public synchronized void start() {
        // 是否开启监控服务
        if (enabled) {
            // 开启AKKA服务
            actorSystem = ActorSystem.create(systemName, ConfigUtils.getConfig().getConfig("akka").atPath("akka"));
            // state 状态服务
            actorSystem.actorOf(Props.create(StatusActor.class, live), "state");
            if (target.getBoolean("enabled")) {
                timer = new Timer("timer-monitor");
                String targetHost = target.getString("host", Sistem.getHost());
                int targetPort = target.getInteger("port");
                String targetName = target.getString("systemname");
                String targetRegName = target.getString("registername");
                String targetActorName = target.getString("monitorname");
                int period = target.getInteger("period", 5000);
                // 注册
                ActorSelection regSelection = actorSystem.actorSelection(MixedUtils.formatActorPath(targetName,
                        targetHost, targetPort, targetRegName));
                LassockInformation info = Sistem.getInformation();
                Timeout timeout = new Timeout(Duration.create(100, "seconds"));
                Future<Object> future = Patterns.ask(regSelection, info, timeout);
                try {
                    String result = (String) Await.result(future, timeout.duration());
                    if (!result.equals("successful")) {
                        _LOG.error("register lassock error");
                        _LOG.error(result);
                        System.exit(0);
                    }
                } catch (Exception e) {
                    _LOG.error("regiester lassock error,so the lassock will shutdown", e);
                    System.exit(0);
                }

                // 任务监控
                selection = actorSystem.actorSelection(MixedUtils.formatActorPath(targetName, targetHost, targetPort,
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
        LassockState state = live.getState();
        // state.setState(RunState.SHUTDOWN);
        selection.tell(state, ActorRef.noSender());
        actorSystem.shutdown();
        super.stop();
    }

    /**
     * 状态操作Actor
     *
     * @author ruibing.zhao
     * @version 1.0
     * @since 2016年2月22日
     */
    public static class StatusActor extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);
        Live live;

        public StatusActor(Live live) {
            this.live = live;
        }

        public void onReceive(Object message) throws Exception {
            if (message instanceof RunState) {
                RunState state = (RunState) message;
                switch (state) {
                    case RUNNING:
                        if (live.isPaused())
                            live.resume();
                        break;
                    case SHUTDOWN:
                        log.info("Remote shutdown");
                        System.exit(0);
                        break;
                    case PAUSED:
                        if (!live.isPaused())
                            live.pause();
                        break;
                    default:
                        break;
                }
            }
            // 返回状态
            List<Intelligence> is = live.getIntelligences();
            LassockState state = live.getState();
            IntelligenceLetter letter = new IntelligenceLetter(state, is);
            getSender().tell(letter, ActorRef.noSender());
        }
    }
}
