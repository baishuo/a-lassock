package com.aleiye.raker.live.conf;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * 针对采集数据添回数据
 * 
 * @author ruibing.zhao
 * @since 2015年11月17日
 */
public class AttributeAware {
	// 其它属性
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Object getAttribute(@Nonnull String key) {
		return attributes.get(key);
	}

	public void addAttribute(@Nonnull String key, Object value) {
		attributes.put(key, value);
	}

	public boolean containsAttribute(@Nonnull String key) {
		return attributes.containsKey(key);
	}

	public void addAllAttributes(@Nonnull Map<String, ?> map) {
		attributes.putAll(map);
	}
}
