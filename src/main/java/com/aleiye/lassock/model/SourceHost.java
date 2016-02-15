package com.aleiye.lassock.model;

import java.io.Serializable;

/**
 * Created by ywt on 15/5/28.
 */
public class SourceHost implements Serializable {

	private static final long serialVersionUID = -8204996321215858248L;

	// 设备名称
	String deviceName;
	// 设备ip地址
	String deviceIp;
	// 设备所属用户
	String belongUserId;
	// 设备唯一标识
	String deviceMac;
	// 设备状态
	String sts;

	public SourceHost(String deviceName, String deviceIp, String belongUserId, String deviceMac, String sts) {
		super();
		this.deviceName = deviceName;
		this.deviceIp = deviceIp;
		this.belongUserId = belongUserId;
		this.deviceMac = deviceMac;
		this.sts = sts;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getBelongUserId() {
		return belongUserId;
	}

	public void setBelongUserId(String belongUserId) {
		this.belongUserId = belongUserId;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	@Override
	public String toString() {
		return "SourceHost [deviceName=" + deviceName + ", deviceIp=" + deviceIp + ", belongUserId=" + belongUserId
				+ ", deviceMac=" + deviceMac + ", sts=" + sts + "]";
	}
}
