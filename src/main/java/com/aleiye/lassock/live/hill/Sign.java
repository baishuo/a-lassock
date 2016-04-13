package com.aleiye.lassock.live.hill;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Course.RunType;
import com.aleiye.lassock.api.conf.ValueStation;
import com.aleiye.lassock.live.hill.shade.AbstractShade;

/**
 * 采集标识 <br>
 * 根据课程(<tt>Course</tt>)生成,课程可生成多个采集标识.
 * 同时当多个课程存在共性需要合并时采集时,标识可关联多个课程,
 * 两者为多对多关系
 * <p>
 * 该类为通用采集标识,不同采集类型状态不同时,需继承该类扩展 <tt>Shade</tt>创建必须依赖标识对像,每一个Shade都对应一个唯一标识
 * 
 * @author ruibing.zhao
 * @since 2015年5月20日
 * @version 2.1.2
 * @see Course
 * @see AbstractShade
 */
public class Sign extends ValueStation {
	// 关个多个课程时拼接字符
	public static final String JOIN_CHAR = ",";
	
	private Course course;
	// 唯一标识
	private String id;

	private String name;
	// 采集类型
	private String type;
	// 编码
	private String encoding = "UTF-8";
	// 是否移除
	private boolean removed = false;
	// 是否丢失
	private boolean lost = false;

	private String basketName;

	// 关联课程配置ID列表
	private List<String> courseIdList = new ArrayList<String>();
	// 配置ID列表 以 JOIN_CHAR 拼接
	private volatile String courseIds;

	/** 运行方式 */
	private RunType runType = RunType.DEFAULT;

	private long delay = 3000;

	private long period = 10000;

	private String cron;

	private String groupName;

	/**
	 * 关联课程个数
	 * @return
	 */
	public int associateSize() {
		return this.courseIdList.size();
	}

	/**
	 * 关联一个课程ID
	 * 表示该Shade 同属于多个课程ID,只用读一次
	 * 
	 * @param cid
	 */
	public void associate(String cid) {
		if (!courseIdList.contains(cid)) {
			courseIdList.add(cid);
			courseIds = StringUtils.join(courseIdList, JOIN_CHAR);
		}
	}

	/**
	 * 解除一个课程ID关联
	 * 
	 * @param cid
	 */
	public void disassociate(String cid) {
		if (courseIdList.contains(cid)) {
			courseIdList.remove(cid);
			courseIds = StringUtils.join(courseIdList, JOIN_CHAR);
		}
	}

	public List<String> getCourseIdList() {
		return courseIdList;
	}

	public String getCourseIds() {
		return courseIds;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return "";
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public boolean isLost() {
		return lost;
	}

	public void setLost(boolean lost) {
		this.lost = lost;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBasketName() {
		return basketName;
	}

	public void setBasketName(String basketName) {
		this.basketName = basketName;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
}
