package com.aleiye.lassock.live.scroll;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.aleiye.lassock.annotations.Required;
import com.aleiye.lassock.common.Context;

/**
 * 采集课程
 * 
 * @author ruibing.zhao
 * @since 2015年5月25日
 * @version 2.1.2
 */
public class Course extends Context implements Cloneable {
	// 课程唯一标识
	@Required
	private String id;
	@Required
	// 课程名称
	private String name;
	// 采集类型
	// 大类:text,SYSLOG,SNMP,JDBC,TELNET等
	@Required
	private String type;
	// 子类型
	// SYSLOG[TCP,UDP];SNMP[TRAP,FLOW,CPU,MEMORY,TEMPERATURE]
	private String subType;

	// 数传输方式
	private String transportType = null;
	// 对列名
	private String basketName;

	// 用户ID
	private String userId;

	// 是否失败
	private boolean failure = false;

	private Exception exception;

	/** 运行方式 */
	private RunType runType = RunType.DEFAULT;

	private long delay = 3000;

	private long period = 10000;

	private String cron;

	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public static enum RunType {
		DEFAULT, TIMER, SCHEDULE, CRON, GROUP
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFailure() {
		return failure;
	}

	public void setFailure(boolean failure) {
		this.failure = failure;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	@Override
	public Course clone() throws CloneNotSupportedException {
		Course c = (Course) super.clone();
		Map<String, Object> p = this.getParameters();
		c.setParameters(new HashMap<String, Object>());
		for (Entry<String, Object> entry : p.entrySet()) {
			c.put(entry.getKey(), entry.getValue());
		}
		c.setAttributes(new HashMap<String, Object>());
		c.addAllAttributes(this.getAttributes());

		return c;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RunType getRunType() {
		return runType;
	}

	public void setRunType(RunType runType) {
		this.runType = runType;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getBasketName() {
		return basketName;
	}

	public void setBasketName(String basketName) {
		this.basketName = basketName;
	}
}
