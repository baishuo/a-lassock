package com.aleiye.lassock.backup.dbcs;

import java.beans.PropertyVetoException;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DB collection 管理
 * 
 * @author ruibing.zhao
 * @since 2015年8月24日
 * @version 2.1.2
 */
public class DBManager {
	private static Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
	private static int minPoolSize = 5;
	private static int maxPoolSize = 30;
	private static int initialPoolSize = 10;
	private static int maxIdleTime = 60;
	private static int acquireIncrement = 5;
	private static int maxStatements = 0;
	private static int idleConnectionTestPeriod = 60;
	private static int acquireRetryAttempts = 30;
	private static boolean breakAfterAcquireFailure = true;
	private static boolean testConnectionOnCheckin = true;

	private static ConcurrentHashMap<String, ComboPooledDataSource> dataSources;

//	static {
//		dataSources = new ConcurrentHashMap<String, ComboPooledDataSource>();
//		Properties prop = new Properties();
//
//		File file = new File("properties/c3p0-config.properties");
//		try {
//			if (file.exists()) {
//				FileInputStream fis;
//
//				fis = new FileInputStream(file);
//				prop.load(fis);
//				minPoolSize = Integer.parseInt(prop.getProperty("c3p0.minPoolSize"));
//				maxPoolSize = Integer.parseInt(prop.getProperty("c3p0.maxPoolSize"));
//				initialPoolSize = Integer.parseInt(prop.getProperty("c3p0.initialPoolSize"));
//				maxIdleTime = Integer.parseInt(prop.getProperty("c3p0.maxIdleTime"));
//				acquireIncrement = Integer.parseInt(prop.getProperty("c3p0.acquireIncrement"));
//				maxStatements = Integer.parseInt(prop.getProperty("c3p0.maxStatements"));
//				idleConnectionTestPeriod = Integer.parseInt(prop.getProperty("c3p0.idleConnectionTestPeriod"));
//				acquireRetryAttempts = Integer.parseInt(prop.getProperty("c3p0.acquireRetryAttempts"));
//				breakAfterAcquireFailure = Boolean.parseBoolean(prop.getProperty("c3p0.breakAfterAcquireFailure"));
//				testConnectionOnCheckin = Boolean.parseBoolean(prop.getProperty("c3p0.testConnectionOnCheckin"));
//
//			}
//			// 加载自定义扩展采集源
//			Config config = ConfigUtils.getConfig().getConfig("datasource");
//			for (int i = 0; i < Integer.MAX_VALUE; i++) {
//				Config dbconfig;
//				try {
//					dbconfig = config.getConfig("db" + i);
//				} catch (Exception e) {
//					break;
//				}
//				CollectionBean bean = new CollectionBean();
//				bean.setName(dbconfig.getString("name"));
//				bean.setDriverClass(dbconfig.getString("driver"));
//				bean.setUrl(dbconfig.getString("url"));
//				bean.setUser(dbconfig.getString("user"));
//				bean.setPassword(dbconfig.getString("password"));
//				registerCollection(bean);
//			}
//
//		} catch (Exception e) {
//			LOGGER.error("c3p0-config read Failed!");
//			LOGGER.debug("", e);
//			System.exit(1);
//		}
//	}

	public static void registerCollection(CollectionBean bean) {
		ComboPooledDataSource dataSource = new ComboPooledDataSource(bean.getName());
		try {
			dataSource.setDriverClass(bean.getDriverClass());

			dataSource.setMinPoolSize(minPoolSize);
			dataSource.setMaxPoolSize(maxPoolSize);
			dataSource.setInitialPoolSize(initialPoolSize);
			dataSource.setMaxIdleTime(maxIdleTime);
			dataSource.setAcquireIncrement(acquireIncrement);
			dataSource.setMaxStatements(maxStatements);
			dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
			dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
			dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
			dataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
			dataSource.setJdbcUrl(bean.getUrl());
			dataSource.setUser(bean.getUser());
			dataSource.setPassword(bean.getPassword());
			dataSources.put(bean.getName(), dataSource);
		} catch (PropertyVetoException e) {
			LOGGER.error("DataSource " + bean.getName() + "register failed!", e);
		}
	}

	public static DataSource getDataSource(String dataSourceName) {
		return dataSources.get(dataSourceName);
	}
}
