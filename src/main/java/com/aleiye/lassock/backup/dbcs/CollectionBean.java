package com.aleiye.lassock.backup.dbcs;

/**
 * DataSource初使化属性
 * 
 * @author ruibing.zhao
 * @since 2015年8月24日
 * @version 2.1.2
 */
public class CollectionBean {
	// 连接名称
	private String name;
	// 驱动
	private String driverClass;
	// 连接URL
	private String url;
	// 用户
	private String user;
	// 密码
	private String password;

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
