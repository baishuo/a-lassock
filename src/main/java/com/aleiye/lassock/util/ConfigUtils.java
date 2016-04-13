package com.aleiye.lassock.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.aleiye.common.config.ConfigFence;
import com.aleiye.lassock.api.conf.Context;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

/**
 * 获取系统配置的主类
 * 
 * @author ruibing.zhao
 * @since 2016年2月25日
 * @version 1.0
 */
public class ConfigUtils {
	private static Config config = ConfigFence.load();

	private ConfigUtils() {}

	public static Config getConfig() {
		return config;
	}

	public static void setConfig(Config newConfig) {
		config = newConfig;
	}

	public static Properties getKafkaProp() {
		Config kafkaConfig = config.getConfig("kafka");
		Properties prop = new Properties();
		for (Map.Entry<String, ConfigValue> entry : kafkaConfig.entrySet()) {
			prop.put(entry.getKey(), kafkaConfig.getString(entry.getKey()));
		}
		return prop;
	}

	/**
	 * 将tapesafe转换为Context
	 * @param config
	 * @return
	 */
	public static Context toContext(Config config) {
		Context contxt = new Context();
		if (config != null)
			for (Entry<String, ConfigValue> entry : config.entrySet()) {
				String value = entry.getValue().unwrapped().toString();
				contxt.put(entry.getKey(), value);
			}
		return contxt;
	}

	/**
	 * 获取Context形式Config
	 * @param keyPath
	 * @return
	 */
	public static Context getContext(String keyPath) {
		try {
			Config configVlaue = config.getConfig(keyPath);
			return toContext(configVlaue);
		} catch (Exception e) {
			;
		}
		return new Context();
	}
}
