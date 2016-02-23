package com.aleiye.lassock.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.aleiye.lassock.live.conf.Context;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

/**
 * 获取系统配置的主类
 * Created by ywt on 15/5/9.
 */
public class ConfigUtils {

	private static Config config = ConfigFactory.load();

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

	public static Context toContext(Config config) {
		Context contxt = new Context();
		if (config != null)
			for (Entry<String, ConfigValue> entry : config.entrySet()) {
				contxt.put(entry.getKey(), entry.getValue().unwrapped().toString());
			}
		return contxt;
	}

	/**
	 * 获取Context形式Config
	 * @param keyPath
	 * @return
	 */
	public static Context getContext(String keyPath) {
		Context contxt = new Context();
		try {
			Config configVlaue = config.getConfig(keyPath);
			if (configVlaue != null)
				for (Entry<String, ConfigValue> entry : configVlaue.entrySet()) {
					contxt.put(entry.getKey(), entry.getValue().unwrapped().toString());
				}
		} catch (Exception e) {
			;
		}
		return contxt;
	}
}
