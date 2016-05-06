package com.aleiye.lassock.live.hill.source.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.hill.source.AbstractEventTrackSource;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * SQL采集
 * @author ruibing.zhao
 * @since 2015年8月27日
 * @version 2.1.2
 */
public class JdbcSource extends AbstractEventTrackSource {
	JdbcParam param;

	@Override
	protected void doPick() throws Exception {
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			s = con.createStatement();
			rs = s.executeQuery(param.getSql());
			ResultSetMetaData rsmd = rs.getMetaData();
			int conut = rsmd.getColumnCount();
			List<Map<String, Object>> rsall = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>(conut);
				for (int i = 1; i < conut + 1; i++) {
					Object value = rs.getObject(i);
					if (value == null) {
						continue;
					}
					row.put(rsmd.getColumnName(i), value);
				}
				rsall.add(row);
			}
			Mushroom mushroom = MushroomBuilder.withBody(rsall, null);
			mushroom.getHeaders().put("size", String.valueOf(rsall.size()));
			putMushroom(mushroom);
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (con != null)
				con.close();
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(param.getUrl(), param.getUsername(), param.getPassword());
	}

	@Override
	protected void doConfigure(Course course) throws Exception {
		this.param = ScrollUtils.forParam(course, JdbcParam.class);
		Class.forName(param.getDriver()); // 加载sql驱动
	}

	@Override
	protected void doStart() throws Exception {
		;
	}

	@Override
	protected void doStop() throws Exception {
		;
	}
}
