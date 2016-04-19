package com.aleiye.lassock.live.conf.source;

import com.aleiye.lassock.live.hill.source.Source;
import com.aleiye.lassock.live.hill.source.jdbc.JdbcSource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpCpuSource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpFlowSource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpMemorySource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpStandardSource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpTemperatureSource;
import com.aleiye.lassock.live.hill.source.snmp.SnmpTrapSource;
import com.aleiye.lassock.live.hill.source.syslog.SyslogSource;
import com.aleiye.lassock.live.hill.source.telnet.Telnet2Source;
import com.aleiye.lassock.live.hill.source.telnet.TelnetJumpSource;
import com.aleiye.lassock.live.hill.source.telnet.TelnetSource;
import com.aleiye.lassock.live.hill.source.text.TextSource;

/**
 * 系统中可供选择的采集类型的枚举
 * 
 * @author ruibing.zhao
 * @since 2016年4月19日
 * @version 1.0
 */
public enum SourceType {
	/**
	 * Place holder for custom sources not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * SYSLOG Source
	 */
	SYSLOG(SyslogSource.class),

	/**
	 * SYSLOG UDP Source
	 */
	SNMP(SnmpStandardSource.class),

	/**
	 * SNMP TRAP Source
	 */
	SNMP_TRAP(SnmpTrapSource.class),

	/**
	 * 流量 Source
	 */
	SNMP_FLOW(SnmpFlowSource.class),

	/**
	 * 内存 Source
	 */
	SNMP_MEMORY(SnmpMemorySource.class),

	/**
	 * CPU Source
	 */
	SNMP_CPU(SnmpCpuSource.class),

	/**
	 * 温度 Source
	 */
	SNMP_TEMPERATURE(SnmpTemperatureSource.class),

	/**
	 * JDBC Source
	 */
	JDBC(JdbcSource.class),

	/**
	 * TELNET Source
	 */
	TELNET(TelnetSource.class), TELNET2(Telnet2Source.class),
	/**
	 * TEXT file Source
	 */
	TEXT(TextSource.class),
	/**
	 * TELNET Source
	 */
	TELNET_JUMP(TelnetJumpSource.class);

	private final Class<? extends Source> sourceClass;

	private SourceType(Class<? extends Source> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Class<? extends Source> getSourceClass() {
		return sourceClass;
	}
}
