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
	protected Map<String, Object> values = new HashMap<String, Object>();

	public Map<String, Object> getValues() {
		return values;
	}

	public void setVallues(Map<String, Object> attributes) {
		this.values = attributes;
	}

	public Object getVallue(@Nonnull String key) {
		return values.get(key);
	}

	public void addVallue(@Nonnull String key, Object value) {
		values.put(key, value);
	}

	public boolean containsVallue(@Nonnull String key) {
		return values.containsKey(key);
	}

	public void addAllVallues(@Nonnull Map<String, ?> map) {
		values.putAll(map);
	}
}
