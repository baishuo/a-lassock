package com.aleiye.lassock.lang;

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

	public static final String HOST;

	public static final String IP;

	public static final String MAC;

	static {
		HOST = SigarUtils.getHostsNameBySigar();
		IP = SigarUtils.getIPBySigar();
		MAC = SigarUtils.getMacBySigar();
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
	public static OSType getSystem() {
		String name = System.getProperty("os.name");
		if (name.indexOf("Windows") > -1) {
			return OSType.WINDOWS;
		} else if (name.indexOf("Linux") > -1) {
			return OSType.LINUX;
		} else if (name.indexOf("Mac") > -1) {
			return OSType.MAC;
		} else if (name.indexOf("Unix") > -1) {
			return OSType.UNIX;
		} else {
			return OSType.OTHER;
		}
	}
}
