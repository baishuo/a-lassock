package com.aleiye.lassock.util;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang.StringUtils;

/**
 * Created by ywt on 15/5/18.
 */
public class AkkaUtils {

    public static ActorSystem createActorSystem(String host, int port, String systemName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("|akka.daemonic = on");
        buffer.append("|akka.loggers = [\"akka.event.slf4j.Slf4jLogger\"]");
        buffer.append("|akka.stdout-loglevel = \"INFO\"");
        buffer.append("|akka.daemonic = on");
        buffer.append("|akka.actor.provider=\"akka.remote.RemoteActorRefProvider\"");
        buffer.append("|akka.remote.netty.tcp.transport-class = \"akka.remote.transport.netty.NettyTransport\"");
        buffer.append("|akka.remote.netty.tcp.hostname=\"").append(host).append("\"");
        buffer.append("|akka.remote.netty.tcp.port=").append(port);
        buffer.append("|akka.remote.netty.tcp.tcp-nodelay = on");
        Config akkaConfig = ConfigFactory.parseString(stripMargin(buffer.toString(), '|'));
        return ActorSystem.create(systemName, akkaConfig);
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

    public static String getRemoteActorPath(String host, int port, String systemName,String actorName) {
        String remotePath = String.format("akka.tcp://%s@%s:%s/user/%s", systemName, host, port, actorName);
        return remotePath;
    }
}
