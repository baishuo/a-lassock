package com.aleiye.raker.util;

/**
 * @author ruibing.zhao
 * @since 2015年10月13日
 */
public class SysUtil {
	public static enum OSType {
		WINDOW, LINUX, UNIX, MAC, OTHER
	}

	/**
	 * 系统
	 * @return
	 */
	public static OSType getSystem() {
		String name = System.getProperty("os.name");
		if (name.indexOf("Windows") > -1) {
			return OSType.WINDOW;
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
