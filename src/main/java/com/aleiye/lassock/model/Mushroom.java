package com.aleiye.lassock.model;

import com.aleiye.lassock.common.Context;

/**
 * (蘑茹)采集产出
 *
 * @author ruibing.zhao
 * @version 2.1.2
 * @since 2015年5月23日
 */
public class Mushroom extends Context {
	private String signId;
	private String enconde = "utf-8";
	// 采集内容
	private Object content;
	private long size;
	private long id;
	private String path;
	private long soffset;
	private long eoffset;

	private int queueIndex = 0;

	public String getEnconde() {
		return enconde;
	}

	public void setEnconde(String enconde) {
		this.enconde = enconde;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSoffset() {
		return soffset;
	}

	public void setSoffset(long soffset) {
		this.soffset = soffset;
	}

	public long getEoffset() {
		return eoffset;
	}

	public void setEoffset(long eoffset) {
		this.eoffset = eoffset;
	}

	public int getQueueIndex() {
		return queueIndex;
	}

	public void setQueueIndex(int queueIndex) {
		this.queueIndex = queueIndex;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
