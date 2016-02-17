package com.aleiye.lassock.model;

import java.util.HashMap;
import java.util.Map;

/**
 * (蘑茹)采集产出
 *
 * @author ruibing.zhao
 * @version 2.1.2
 * @since 2015年5月23日
 */
public class GeneralMushroom implements Mushroom {
	// private String signId;
	// private String enconde = "utf-8";
	// // 采集内容
	// private Object content;
	// private long size;
	// private long id;
	// private String path;
	// private long soffset;
	// private long eoffset;
	//
	// private int queueIndex = 0;
	//
	// public String getEnconde() {
	// return enconde;
	// }
	//
	// public void setEnconde(String enconde) {
	// this.enconde = enconde;
	// }
	//
	// public Object getContent() {
	// return content;
	// }
	//
	// public void setContent(Object content) {
	// this.content = content;
	// }
	//
	// public long getId() {
	// return id;
	// }
	//
	// public void setId(long id) {
	// this.id = id;
	// }
	//
	// public String getPath() {
	// return path;
	// }
	//
	// public void setPath(String path) {
	// this.path = path;
	// }
	//
	// public long getSoffset() {
	// return soffset;
	// }
	//
	// public void setSoffset(long soffset) {
	// this.soffset = soffset;
	// }
	//
	// public long getEoffset() {
	// return eoffset;
	// }
	//
	// public void setEoffset(long eoffset) {
	// this.eoffset = eoffset;
	// }
	//
	// public int getQueueIndex() {
	// return queueIndex;
	// }
	//
	// public void setQueueIndex(int queueIndex) {
	// this.queueIndex = queueIndex;
	// }
	//
	// public String getSignId() {
	// return signId;
	// }
	//
	// public void setSignId(String signId) {
	// this.signId = signId;
	// }
	//
	// public long getSize() {
	// return size;
	// }
	//
	// public void setSize(long size) {
	// this.size = size;
	// }
	// 采集内容
	private Object body;
	// 头信息 采集环境信息
	private Map<String, Object> headers = new HashMap<String, Object>();
	// 原有值 配置下发时自带内容值
	private Map<String, String> originalValues;

	@Override
	public Map<String, Object> getHeaders() {
		return headers;
	}

	// @Override
	// public void setHeaders(Map<String, String> headers) {
	// this.headers = headers;
	// }

	@Override
	public Object getBody() {
		return body;
	}

	@Override
	public void setBody(Object body) {
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
}
