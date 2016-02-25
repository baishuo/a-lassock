package com.aleiye.lassock.live.hill.shade.jdbc;

import com.aleiye.lassock.live.hill.Sign;

/**
 * JDBC 采集标识
 * 
 * @author ruibing.zhao
 * @since 2015年8月22日
 * @version 2.1.2
 */
public class JdbcSign extends Sign {
	private String driver;
	private String url;
	private String username;
	private String password;
	// 数据源
	private String dataSource;
	// 要采集的表
	private String sql;

	@Override
	public String getDescription() {
		return dataSource + "/[" + sql + "]";
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
