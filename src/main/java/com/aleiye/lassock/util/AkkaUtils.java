package com.aleiye.lassock.util;


/**
 * Akka System
 * 
 * @author ruibing.zhao
 * @since 2016年2月25日
 * @version 1.0
 */
public class AkkaUtils {
	public static String getRemoteActorPath(String host, int port, String systemName, String actorName) {
		String remotePath = String.format("akka.tcp://%s@%s:%s/user/%s", systemName, host, port, actorName);
		return remotePath;
	}
}
