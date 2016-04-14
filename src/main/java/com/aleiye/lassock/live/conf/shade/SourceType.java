package com.aleiye.lassock.live.conf.shade;

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

/**
 * Enumeration of built in shade types available in the system.
 */
public enum SourceType {
	/**
	 * Place holder for custom sources not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * SYSLOG Shade
	 */
	SYSLOG(SyslogSource.class),

	/**
	 * SYSLOG UDP Shade
	 */
	SNMP(SnmpStandardSource.class),

	/**
	 * SNMP TRAP Shade
	 */
	SNMP_TRAP(SnmpTrapSource.class),

	/**
	 * 流量 Shade
	 */
	SNMP_FLOW(SnmpFlowSource.class),

	/**
	 * 内存 Shade
	 */
	SNMP_MEMORY(SnmpMemorySource.class),

	/**
	 * CPU Shade
	 */
	SNMP_CPU(SnmpCpuSource.class),

	/**
	 * 温度 Shade
	 */
	SNMP_TEMPERATURE(SnmpTemperatureSource.class),

	/**
	 * JDBC Shade
	 */
	JDBC(JdbcSource.class),

	/**
	 * TELNET Shade
	 */
	TELNET(TelnetSource.class), TELNET2(Telnet2Source.class),

	/**
	 * TELNET Shade
	 */
	TELNET_JUMP(TelnetJumpSource.class);

	private final Class<? extends Source> sourceClass;

	private SourceType(Class<? extends Source> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Class<? extends Source> getShadeClass() {
		return sourceClass;
	}
}
