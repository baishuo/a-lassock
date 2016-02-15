package com.aleiye.raker.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import java.util.Map;
import java.util.Properties;

/**
 * 获取系统配置的主类
 * Created by ywt on 15/5/9.
 */
public class ConfigUtils {

    private static Config config = ConfigFactory.load();

    public static Config getConfig(){
        return config;
    }

    public static void setConfig(Config newConfig){
        config = newConfig;
    }

    public static Properties getKafkaProp(){
        Config kafkaConfig = config.getConfig("kafka");
        Properties prop = new Properties();
        for(Map.Entry<String, ConfigValue> entry : kafkaConfig.entrySet()){
            prop.put(entry.getKey(),kafkaConfig.getString(entry.getKey()));
        }
        return prop;
    }
}
