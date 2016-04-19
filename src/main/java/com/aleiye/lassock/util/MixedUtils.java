package com.aleiye.lassock.util;

/**
 * 杂务工具
 * 
 * @author ruibing.zhao
 * @since 2016年2月25日
 * @version 1.0
 */
public class MixedUtils {
	/**
	 * 使用akka系统名，主机名，端口和actor句格式成akka请求路径
	 * 
	 * @param systemName akka 系统名
	 * @param host actor 主机
	 * @param port 端口
	 * @param actorName actor 名称
	 * @return
	 */
	public static String formatActorPath(String systemName, String host, int port, String actorName) {
		String remotePath = String.format("akka.tcp://%s@%s:%s/user/%s", systemName, host, port, actorName);
		return remotePath;
	}
}
