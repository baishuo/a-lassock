package com.aleiye.lassock.api;

import java.util.HashMap;

import com.aleiye.lassock.live.annotation.Required;
import com.aleiye.lassock.live.conf.ValueStation;

/**
 * 采集课程
 * 
 * @author ruibing.zhao
 * @since 2015年5月25日
 * @version 2.1.2
 */
public class Course extends ValueStation implements Cloneable {
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
	// 对列名
	private String basketName;

	/** 运行方式 */
	private RunType runType = RunType.DEFAULT;
	// 延迟时间
	private long delay = 3000;
	// 周期时间
	private long period = 10000;
	// Cron 执行时间
	private String cron;
	// 组名
	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Course clone() throws CloneNotSupportedException {
		Course c = (Course) super.clone();
		// Map<String, Object> p = this.getParameters();
		// c.setParameters(new HashMap<String, Object>());
		// for (Entry<String, Object> entry : p.entrySet()) {
		// c.put(entry.getKey(), entry.getValue());
		// }
		c.setVallues(new HashMap<String, String>());
		c.addAllVallues(this.getValues());

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

	/**
	 * 运行方式
	 * 
	 * @author ruibing.zhao
	 * @since 2016年2月15日
	 * @version 1.0
	 */
	public static enum RunType {
		DEFAULT, TIMER, SCHEDULE, CRON, GROUP, TEXT
	}
}
