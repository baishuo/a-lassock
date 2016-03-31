package com.aleiye.lassock.live.hill.shade.snmp;

import java.util.List;

import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.scroll.CourseConst;

/**
 * SNMP 采集标识
 * 
 * @author ruibing.zhao
 * @since 2015年6月8日
 * @version 2.1.2
 */
public class SnmpSign extends Sign {
	/** 通用 */
	// 协议
	private String protocol = CourseConst.PROTOCOL_UDP;
	// 端口
	private int port = 161;
	/** 标准 SNMP */
	// IP
	private String host;
	private String community;
	private String os;
	// SNMP 版本
	private int version = 1;
	// 采集信息OID集
	private List<String> oids;
	// 同异步
	private boolean syn = false;

	private String usrName;
	private String password;

	@Override
	public String getDescription() {
		return protocol + "/" + host + ":" + port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean isSyn() {
		return syn;
	}

	public void setSyn(boolean syn) {
		this.syn = syn;
	}

	public String getUsrName() {
		return usrName;
	}

	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getOids() {
		return oids;
	}

	public void setOids(List<String> oids) {
		this.oids = oids;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
