package com.aleiye.lassock.test;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.monitor.DefultMonitor.StatusActor;
import com.aleiye.lassock.util.AkkaUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaTest {
	@Test
	public void testParse() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("|akka.daemonic = on");
		buffer.append("|akka.loggers = [\"akka.event.slf4j.Slf4jLogger\"]");
		buffer.append("|akka.stdout-loglevel = \"INFO\"");
		buffer.append("|akka.daemonic = on");
		buffer.append("|akka.actor.provider=\"akka.remote.RemoteActorRefProvider\"");
		buffer.append("|akka.remote.netty.tcp.transport-class = \"akka.remote.transport.netty.NettyTransport\"");
		// TODO
		buffer.append("|akka.remote.netty.tcp.hostname=\"").append("10.0.1.35").append("\"");
		// TODO
		buffer.append("|akka.remote.netty.tcp.port=").append(9982);
		buffer.append("|akka.remote.netty.tcp.tcp-nodelay = on");
		Config akkaConfig = ConfigFactory.parseString(stripMargin(buffer.toString(), '|'));
		ActorSystem system = ActorSystem.create("collectorMonitor", akkaConfig);
		ActorSelection selection = system.actorSelection(AkkaUtils.getRemoteActorPath("10.0.1.35", 9981, "lassock",
				"getStatus"));
		ActorRef greeter = system.actorOf(Props.create(ReturnActor.class), "statusCallback");
		selection.tell(new HashMap(), greeter);
		int i = 0;
		while (true) {
			i += 1000;
			Thread.sleep(1000);
			if (i > 100000)
				break;
		}
	}

	public static class ReturnActor extends UntypedActor {
		LoggingAdapter log = Logging.getLogger(getContext().system(), this);

		public void onReceive(Object message) throws Exception {
			System.out.println(message.toString());
		}
	}

	public static String stripMargin(String configStr, char marginChar) {
		StringBuilder builder = new StringBuilder();
		String[] splits = StringUtils.split(configStr, marginChar);
		for (String split : splits) {
			if (!"".equals(split)) {
				builder.append(split).append("\r\n");
			}
		}
		return builder.toString();
	}
}
