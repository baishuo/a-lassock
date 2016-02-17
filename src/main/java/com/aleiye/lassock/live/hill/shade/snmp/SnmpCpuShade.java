package com.aleiye.lassock.live.hill.shade.snmp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;

import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.model.GeneralMushroom;

/**
 * CPU 使用获取
 * 
 * @author ruibing.zhao
 * @since 2015年8月30日
 * @version 2.1.2
 */
public class SnmpCpuShade extends SnmpStandardShade {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnmpCpuShade.class);

	private String cpuOid;

	// cpu
	public void cpu(Snmp snmp, Target target) throws Exception {
		VariableBinding cpmCPUTotal1min = query(snmp, target, cpuOid, PDU.GET);
		if (cpmCPUTotal1min == null) {
			LOGGER.warn("No such instance|object target:" + target.getAddress() + " type:" + PDU.GET + " OID:" + cpuOid);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("A_logtype", this.sign.getSubType());
		map.put("host", this.sign.getHost());
		map.put("cpuLoad", cpmCPUTotal1min.getVariable().toString());
		StringBuffer buffer = new StringBuffer();
		buffer.append(cpmCPUTotal1min.getVariable().toString());// 前流量
		map.put("A_message", buffer.toString());
		map.putAll(sign.getValues());
		GeneralMushroom generalMushroom = new GeneralMushroom();
		generalMushroom.setBody(map);
		putMushroom(sign, generalMushroom);
	}

	@Override
	protected void doConfigure(Context context) throws Exception {
		super.doConfigure(context);
		cpuOid = this.sign.getOids().get(0);
	}

	@Override
	protected void doSend() throws Exception {
		cpu(this.snmp, this.target);
	}
}
