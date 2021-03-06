package com.aleiye.lassock.live.model;

import java.util.Map;

/**
 * 采集产出接口
 * 
 * @author ruibing.zhao
 * @since 2016年2月17日
 * @version 1.0
 */
public interface Mushroom {
	// 返回采集环境相关信息
	public Map<String, String> getHeaders();

	public void setHeaders(Map<String, String> headers);

	// 采集内容
	public byte[] getBody();

	// 设置采集内容
	public void setBody(byte[] body);

	// 采集任务下发自带值获取
	public Map<String, String> getOriginalValues();

	// 设置采集任务下发自带值获取
	public void setOriginalValues(Map<String, String> values);

	public void incrementCompleteCount();

	public void incrementFailedCount();

}
