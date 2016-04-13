package com.aleiye.lassock.lang;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.aleiye.lassock.api.LassockInformation;
import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.SigarUtils;

/**
 * Lassock 系统信息类
 * 
 * @author ruibing.zhao
 * @since 2016年1月29日
 * @version 1.0
 */
public class Sistem {
	private Sistem() {}

	private static final String name;
	// 本机主机名
	private static final String host;
	// 本机IP
	private static final String ip;
	// 本机MAC
	private static final String mac;

	private static final OSType osType;

	private static final int port;

	private static final Map<String, Object> header;

	static {
		Context system = ConfigUtils.getContext("system");
		// 获取IP
		String ip0 = system.getString("ip");
		ip = StringUtils.isNotBlank(ip0) ? ip0 : SigarUtils.getIPBySigar();
		// 获取HOST
		String host0 = system.getString("host");
		host0 = StringUtils.isNotBlank(host0) ? host0 : SigarUtils.getHostsNameBySigar();
		host = "localhost".equals(host0) ? ip : host0;
		// 获取MAC
		String mac0 = system.getString("mac");
		mac = StringUtils.isNotBlank(mac0) ? mac0 : SigarUtils.getMacBySigar();

		port = system.getInteger("port", 9981);

		// 获取名称
		String name0 = system.getString("name");
		name = StringUtils.isNotBlank(name0) ? name0 : "lassock_" + host;
		String osName = System.getProperty("os.name");
		// 附加属性
		header = system.getSubProperties("header.");
		if (osName.indexOf("Windows") > -1) {
			osType = OSType.WINDOWS;
		} else if (osName.indexOf("Linux") > -1) {
			osType = OSType.LINUX;
		} else if (osName.indexOf("Mac") > -1) {
			osType = OSType.MAC;
		} else if (osName.indexOf("Unix") > -1) {
			osType = OSType.UNIX;
		} else {
			osType = OSType.OTHER;
		}
	}

	public static LassockInformation getInformation() {
		LassockInformation info = new LassockInformation();
		info.setName(name);
		info.setHost(host);
		info.setIp(ip);
		info.setMac(mac);
		info.setPort(port);
		for (Entry<String, Object> entry : header.entrySet()) {
			info.put(entry.getKey(), entry.getValue().toString());
		}
		return info;
	}

	/**
	 * 系统类型
	 * 
	 * @author ruibing.zhao
	 * @since 2016年2月16日
	 * @version 1.0
	 */
	public static enum OSType {
		WINDOWS, LINUX, UNIX, MAC, OTHER
	}

	/**
	 * 系统
	 * @return
	 */
	public static OSType getSysType() {
		return osType;
	}

	public static String getHost() {
		return host;
	}

	public static String getIp() {
		return ip;
	}

	public static String getMac() {
		return mac;
	}

	public static String getLassockname() {
		return name;
	}

	public static Map<String, Object> getHeader() {
		return header;
	}

	public static int getPort() {
		return port;
	}
}
