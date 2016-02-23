package com.aleiye.lassock.test;

import java.io.IOException;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaServer {

	public static void main(String[] args) {
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
		ActorRef greeter = system.actorOf(Props.create(MonitorActor.class), "monitor");
		try {
			char i = (char) System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class MonitorActor extends UntypedActor {
		LoggingAdapter log = Logging.getLogger(getContext().system(), this);

		public void onReceive(Object message) throws Exception {
			JSONArray array = JSONArray.fromObject(message);
			System.out.println(array.toString());
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
