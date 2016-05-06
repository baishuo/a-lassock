package com.aleiye.lassock.live.hill.source.jdbc;

/**
 * JDBC 采集参数
 * 
 * @author ruibing.zhao
 * @since 2015年8月22日
 * @version 2.1.2
 */
public class JdbcParam {
	// 数据库连接字符串
	private String url;
	// JDBC 驱动
	private String driver;
	// 用户名
	private String username;
	// 密码
	private String password;
	// 采集执行语句
	private String sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
