package com.aleiye.lassock.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控情报
 * 
 * @author ruibing.zhao
 * @since 2015年8月5日
 * @version 2.1.2
 */
public class Intelligence implements Serializable {
	private static final long serialVersionUID = 4764682644875679393L;
	private Map<String, Object> header = new HashMap<String, Object>();

	public Intelligence(String name) {
		this.courseName = name;
	}

	// ResouceID
	private final String courseName;
	// 状态
	private ShadeStatus status = ShadeStatus.NORMAL;
	// 采集类型
	private String type;
	// 子类型
	private String subType;
	// 接收数
	private long acceptedCount = 0;
	private long errorCount = 0;
	// 完成数
	private long completeCount = 0;
	// 失败数
	private long failedCount = 0;
	// 监控时间
	private long monitorTime;

	public long getMonitorTime() {
		return monitorTime;
	}

	public void setMonitorTime(long monitorTime) {
		this.monitorTime = monitorTime;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public long getAcceptedCount() {
		return acceptedCount;
	}

	public void setAcceptedCount(long acceptedCount) {
		defaultMonitorTime();
		this.acceptedCount = acceptedCount;
	}

	public long getCompleteCount() {
		return completeCount;
	}

	public void setCompleteCount(long completeCount) {
		defaultMonitorTime();
		this.completeCount = completeCount;
	}

	public long getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(long failedCount) {
		defaultMonitorTime();
		this.failedCount = failedCount;
	}

	public Map<String, Object> getHeader() {
		return header;
	}

	public void setHeader(Map<String, Object> header) {
		synchronized (this) {
			this.header = header;
		}
	}

	public void put(String key, Object value) {
		synchronized (this) {
			this.header.put(key, value);
		}
	}

	public Object get(String key) {
		return this.header.get(key);
	}

	public ShadeStatus getStatus() {
		return status;
	}

	public void setStatus(ShadeStatus status) {
		defaultMonitorTime();
		this.status = status;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		defaultMonitorTime();
		this.errorCount = errorCount;
	}

	void defaultMonitorTime() {
		this.monitorTime = System.currentTimeMillis();
	}
}
