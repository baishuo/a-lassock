package com.aleiye.lassock.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Siger 工具类
 * 
 * @author ruibing.zhao
 * @since 2016年1月29日
 * @version 1.0
 */
public class SigarUtils {
	private static Logger logger = Logger.getLogger(SigarUtils.class);

	/**
	 * 屏蔽类创建构造
	 */
	private SigarUtils() {}

	static {
		// sigar 工具目录
		String sigarPath = SigarUtils.class.getResource("/").getPath() + "sigar";
		File sigarFolder = new File(sigarPath);
		if (!sigarFolder.exists()) {
			sigarPath = SigarUtils.class.getResource("/").getPath() + "config/sigar";
			sigarFolder = new File(sigarPath);
			if (!sigarFolder.exists())
				throw new RuntimeException("Can not find sigar path!");
		}
		// 将sigar 目录添加到 class_path
		String classPath = System.getProperty("java.library.path");
		if (!classPath.contains(sigarPath)) {
			classPath = sigarPath + File.pathSeparator + classPath;
			System.setProperty("java.library.path", classPath);
		}
		logger.debug("java.library.path:" + classPath);
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
	public static String getMac() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInterfaceConfig config = sigar.getNetInterfaceConfig(null);
			String str = config.getHwaddr();
			return str.replaceAll(":", "").replaceAll("-", "");
		} catch (Exception e) {
			logger.error("Sigar get MAC error!", e);
			logger.debug("", e);
		} finally {
			if (sigar != null) {
				sigar.close();
			}
		}
		return null;
	}

	/**
	 * 获取本机IP地址
	 * 
	 * @return
	 * @throws SigarException
	 */
	public static String getIP() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInterfaceConfig config = sigar.getNetInterfaceConfig(null);
			return config.getAddress();
		} catch (Exception e) {
			logger.error("Sigar get IP error!", e);
			logger.debug("", e);
		} finally {
			if (sigar != null) {
				sigar.close();
			}
		}
		return NetFlags.LOOPBACK_ADDRESS;
	}

	/**
	 * 获取本机主机名
	 * 
	 * @return
	 */
	public static String getHostName() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			NetInfo info = sigar.getNetInfo();
			return info.getHostName();
		} catch (Exception e) {
			logger.error("Sigar get host name error!", e);
			logger.debug("", e);
		} finally {
			sigar.close();
		}
		return NetFlags.LOOPBACK_HOSTNAME;
	}
}
