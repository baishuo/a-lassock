package com.aleiye.lassock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指定文件输出LOG 信息
 * 
 * @author ruibing.zhao
 * @since 2015年11月17日
 */
public class LogUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger("courseError");
	private static final Logger INFO_LOGGER = LoggerFactory.getLogger("courseError");
	public static void error(String msg) {
		LOGGER.error(msg);
	}
}
