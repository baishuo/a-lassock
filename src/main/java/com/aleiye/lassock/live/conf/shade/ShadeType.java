package com.aleiye.lassock.live.conf.shade;

import com.aleiye.lassock.live.hill.shade.Shade;
import com.aleiye.lassock.live.hill.shade.jdbc.JdbcShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpCpuShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpFlowShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpMemoryShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpStandardShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpTemperatureShade;
import com.aleiye.lassock.live.hill.shade.snmp.SnmpTrapShade;
import com.aleiye.lassock.live.hill.shade.syslog.SyslogTCPShade;
import com.aleiye.lassock.live.hill.shade.syslog.SyslogUDPShade;
import com.aleiye.lassock.live.hill.shade.telnet.Telnet2Shade;
import com.aleiye.lassock.live.hill.shade.telnet.TelnetJumpShade;
import com.aleiye.lassock.live.hill.shade.telnet.TelnetShade;

/**
 * Enumeration of built in shade types available in the system.
 */
public enum ShadeType {
	/**
	 * Place holder for custom sources not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * SYSLOG TCP Shade
	 */
	SYSLOG_TCP(SyslogTCPShade.class),

	/**
	 * SYSLOG UDP Shade
	 */
	SYSLOG_UDP(SyslogUDPShade.class),

	/**
	 * SYSLOG UDP Shade
	 */
	SNMP(SnmpStandardShade.class),

	/**
	 * SNMP TRAP Shade
	 */
	SNMP_TRAP(SnmpTrapShade.class),

	/**
	 * 流量 Shade
	 */
	SNMP_FLOW(SnmpFlowShade.class),

	/**
	 * 内存 Shade
	 */
	SNMP_MEMORY(SnmpMemoryShade.class),

	/**
	 * CPU Shade
	 */
	SNMP_CPU(SnmpCpuShade.class),

	/**
	 * 温度 Shade
	 */
	SNMP_TEMPERATURE(SnmpTemperatureShade.class),

	/**
	 * JDBC Shade
	 */
	JDBC(JdbcShade.class),

	/**
	 * TELNET Shade
	 */
	TELNET(TelnetShade.class), TELNET2(Telnet2Shade.class),

	/**
	 * TELNET Shade
	 */
	TELNET_JUMP(TelnetJumpShade.class);

	private final Class<? extends Shade> sourceClass;

	private ShadeType(Class<? extends Shade> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Class<? extends Shade> getShadeClass() {
		return sourceClass;
	}
}
