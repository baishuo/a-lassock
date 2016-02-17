package com.aleiye.lassock.live.conf;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * 针对采集数据添加数据
 * 
 * @author ruibing.zhao
 * @since 2015年11月17日
 */
public class ValueStation extends Context {
	// 其它属性
	protected Map<String, String> values = new HashMap<String, String>();

	public Map<String, String> getValues() {
		return values;
	}

	public void setVallues(Map<String, String> attributes) {
		this.values = attributes;
	}

	public String getVallue(@Nonnull String key) {
		return values.get(key);
	}

	public void addVallue(@Nonnull String key, String value) {
		values.put(key, value);
	}

	public boolean containsVallue(@Nonnull String key) {
		return values.containsKey(key);
	}

	public void addAllVallues(@Nonnull Map<String, String> map) {
		values.putAll(map);
	}
}
