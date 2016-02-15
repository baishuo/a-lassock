package com.aleiye.raker.live.hill.shade.jdbc;

import com.aleiye.raker.live.scroll.Sign;

/**
 * JDBC 采集标识
 * 
 * @author ruibing.zhao
 * @since 2015年8月22日
 * @version 2.1.2
 */
public class JdbcSign extends Sign {
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
}
