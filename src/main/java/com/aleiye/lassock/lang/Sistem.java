package com.aleiye.lassock.lang;

import org.apache.commons.lang3.StringUtils;

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

	private static final String lassockName;
	// 本机主机名
	private static final String host;
	// 本机IP
	private static final String ip;
	// 本机MAC
	private static final String mac;

	private static final OSType osType;

	static {
		// 获取HOST
		String host0 = null;
		try {
			host0 = ConfigUtils.getConfig().getString("system.host");
		} catch (Exception e) {
			;
		}
		if (StringUtils.isBlank(host0))
			host = SigarUtils.getHostsNameBySigar();
		else {
			host = host0;
		}
		// 获取IP
		String ip0 = null;
		try {
			ip0 = ConfigUtils.getConfig().getString("system.ip");
		} catch (Exception e) {
			;
		}
		if (StringUtils.isBlank(ip0))
			ip = SigarUtils.getIPBySigar();
		else {
			ip = ip0;
		}
		// 获取MAC
		String mac0 = null;
		try {
			mac0 = ConfigUtils.getConfig().getString("system.ip");
		} catch (Exception e) {
			;
		}
		if (StringUtils.isBlank(mac0))
			mac = SigarUtils.getMacBySigar();
		else {
			mac = mac0;
		}

		// 获取名称
		String name = null;
		try {
			name = ConfigUtils.getConfig().getString("system.name");
		} catch (Exception e) {
			;
		}
		if (StringUtils.isBlank(name))
			lassockName = "lassock_" + host;
		else
			lassockName = name;
		String osName = System.getProperty("os.name");
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
		return lassockName;
	}
}
