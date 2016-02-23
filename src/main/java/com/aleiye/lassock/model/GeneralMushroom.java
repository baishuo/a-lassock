package com.aleiye.lassock.model;

import java.util.HashMap;
import java.util.Map;

import com.aleiye.lassock.api.Intelligence;

/**
 * (蘑茹)采集产出
 *
 * @author ruibing.zhao
 * @version 2.1.2
 * @since 2015年5月23日
 */
public class GeneralMushroom implements Mushroom {

	private Intelligence intelligence;
	// 采集内容
	private byte[] body;
	// 头信息 采集环境信息
	private Map<String, String> headers = new HashMap<String, String>();
	// 原有值 配置下发时自带内容值
	private Map<String, String> originalValues;

	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public byte[] getBody() {
		return body;
	}

	@Override
	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public Map<String, String> getOriginalValues() {
		return originalValues;
	}

	@Override
	public void setOriginalValues(Map<String, String> values) {
		this.originalValues = values;
	}

	@Override
	public void incrementCompleteCount() {
		this.intelligence.setCompleteCount(this.intelligence.getCompleteCount() + 1);
	}

	@Override
	public void incrementFailedCount() {
		this.intelligence.setFailedCount(this.intelligence.getFailedCount() + 1);

	}

}
