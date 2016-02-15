package com.aleiye.lassock.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Siger 工具
 * 
 * @author ruibing.zhao
 * @since 2016年1月29日
 * @version 1.0
 */
public class SigarUtils {
	private static Logger _LOG = Logger.getLogger(SigarUtils.class);

	private SigarUtils() {}

	static {
		String sigarPath = SigarUtils.class.getClassLoader().getResource("").getPath() + "sigar";
		String str = System.getProperty("java.library.path");
		if (!str.contains(sigarPath)) {
			str = sigarPath + File.pathSeparator + str;
			System.setProperty("java.library.path", str);
		}
		_LOG.info("java.library.path " + System.getProperty("java.library.path"));
	}

	public static Sigar getSigar() {
		return new Sigar();
	}

	/**
	 * 返回mac地址 所有的冒号 减号都被过滤
	 * 
	 * @return
	 * @throws SigarException
	 */
	public static String getMacBySigar() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInterfaceConfig netInterfaceConfig = sigar.getNetInterfaceConfig(null);
			String str = netInterfaceConfig.getHwaddr();
			return str.replaceAll(":", "").replaceAll("-", "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sigar != null) {
				sigar.close();
			}
		}
		return null;
	}

	/**
	 * 对外如没有特殊情况都调用这几个bysigar的方法
	 * 
	 * @return
	 * @throws SigarException
	 */
	public static String getIPBySigar() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInterfaceConfig netInterfaceConfig = sigar.getNetInterfaceConfig(null);

			return netInterfaceConfig.getAddress();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sigar != null) {
				sigar.close();
			}
		}
		return "localhost";
	}

	public static String getHostsNameBySigar() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInfo info = sigar.getNetInfo();
			return info.getHostName();
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			return null;
		} finally {
			sigar.close();
		}

	}
}
