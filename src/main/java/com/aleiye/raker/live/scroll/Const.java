package com.aleiye.raker.live.scroll;

/**
 * 该常量类保存课程表{@link Course}所需固定属性key
 * 
 * @author ruibing.zhao
 * @since 2015年6月9日
 * @version 2.1.2
 */
public class Const {
	public static final String PROTOCOL_TCP = "tcp";
	public static final String PROTOCOL_UDP = "udp";

	public static final String RESOURCE_ID = "resourceId";
	public static final String RESOURCE_TYPE = "resourceType";
	public static final String TRANSPORT_TYPE = "transportType";

	/**
	 * 文件采集属性配置
	 * 
	 * @author ruibing.zhao
	 * @since 2015年6月6日
	 * @version 2.1.2
	 */
	public static class text {
		/** 文件采集路径 */
		public static final String DATA_INPUT_PATH = "inputPath";
		/** 正则路径过滤需要有默认值 */
		public static final String PATH_FILTER_REGEX = "pathFilterRegex";
		/** 文件移动目录 */
		public static final String MOVE_PATH = "movePath";
		/** 文件名或目录变更后采集次数 */
		public static final String CHANGED_READ_COUNT = "changedReadCount";
	}

	/**
	 * SYSLOG采集属性配置
	 * 
	 * @author ruibing.zhao
	 * @since 2015年6月6日
	 * @version 2.1.2
	 */
	public static class syslog {
		/**协议*/
		public static final String PROTOCOL = "protocol";
		/**端口*/
		public static final String PORT = "port";
	}

	/**
	 * SNMP采集属性配置
	 * 
	 * @author ruibing.zhao
	 * @since 2015年6月6日
	 * @version 2.1.2
	 */
	public static class snmp {
		/**是否是trap服务 boolean:true false*/
		public static final String IS_TRAP = "trap";
		/**协议*/
		public static final String PROTOCOL = "protocol";
		/**端口*/
		public static final String PORT = "port";

		// ==============================================
		// 非Trap模式下使用以值
		/**主机*/
		public static final String HOST = "host";
		/**community*/
		public static final String COMMUNITY = "community";
		/**系统*/
		public static final String OS = "os";
		/**SNMP版本号 int:1,2,3*/
		public static final String VERSION = "version";

		/**执行周期 long*/
		public static final String PERIOD = "period";
		/**OID List<String>*/
		public static final String OIDS = "oids";
	}

	public static class command {
		// 服务器地址
		public static final String HOST = "host";
		// 端口
		public static final String PORT = "port";
		// 用户名
		public static final String USERNAME = "username";
		// 密码
		public static final String PASSWORD = "password";
		
		public static final String JUMPED = "jumped";

		public static final String PREPARE_COMMAND = "prepareCommand";
		// 执行命令
		public static final String COMMANDS = "commands";
		// 品牌
		public static final String BRAND = "brand";
		// 设备类型（DevType:交换机、路由器、防火墙、服务器、其它）
		public static final String DEVICETYPE = "deviceType";
		// 设备层级（核心、汇聚、接入）
		public static final String DEVICE_FLOOR = "deviceFloor";

		public static final String PERIOD = "period";
	}

	public static class jdbc {
		public static final String DATA_SOURCE_NAME = "dataSource";
		public static final String SQL = "sql";
		public static final String PERIOD = "period";
	}
}
