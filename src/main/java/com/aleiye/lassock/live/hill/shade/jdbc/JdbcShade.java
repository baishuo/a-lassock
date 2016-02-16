package com.aleiye.lassock.live.hill.shade.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.aleiye.lassock.dbcs.DBManager;
import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.live.hill.shade.AbstractPollableShade;
import com.aleiye.lassock.live.scroll.Sign;
import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * SQL采集子源
 * @author ruibing.zhao
 * @since 2015年8月27日
 * @version 2.1.2
 */
public class JdbcShade extends AbstractPollableShade {

	DataSource dataSource;
	// 采集执行语句
	String sql;

	private JdbcSign sign;

	@Override
	public Sign getSign() {
		return sign;
	}

	@Override
	protected void doPick() throws Exception {
		Connection con = dataSource.getConnection();
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int conut = rsmd.getColumnCount();
		List<Map<String, Object>> rsall = new ArrayList<Map<String, Object>>();
		String symbol = String.valueOf(System.currentTimeMillis());
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(conut);
			StringBuffer buf = new StringBuffer();
			for (int i = 1; i < conut + 1; i++) {
				Object value = rs.getObject(i);
				if (value == null) {
					continue;
				}
				buf.append("\t");
				buf.append(value.toString());
				row.put("its_" + rsmd.getColumnName(i), value);
			}
			row.put("isInterface", false);
			row.put("A_message", buf.deleteCharAt(0).toString());
			row.put("A_symbol", symbol);
			rsall.add(row);
		}
		con.close();
		Mushroom mushroom = new Mushroom();
		mushroom.setContent(rsall);
		mushroom.setSize(rsall.size());
		mushroom.put("symbol", symbol);
		putMushroom(sign, mushroom);
	}

	@Override
	protected void doConfigure(Context context) throws Exception {
		this.sign = (JdbcSign) ScrollUtils.forSign(context, JdbcSign.class);
	}

	@Override
	protected void doStart() throws Exception {
		this.dataSource = DBManager.getDataSource(sign.getDataSource());
		sql = sign.getSql();

	}

	@Override
	protected void doStop() throws Exception {
		dataSource = null;
	}
}
