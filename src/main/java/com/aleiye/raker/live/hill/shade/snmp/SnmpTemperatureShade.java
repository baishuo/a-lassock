package com.aleiye.raker.live.hill.shade.snmp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;

import com.aleiye.raker.common.Context;
import com.aleiye.raker.model.Mushroom;

/**
 * SNMP 温度采集
 * 
 * @author ruibing.zhao
 * @since 2015年8月30日
 * @version 2.1.2
 */
public class SnmpTemperatureShade extends SnmpStandardShade {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnmpTemperatureShade.class);

	private String temperatureOid;

	// 温度
	public void temperature(Snmp snmp, Target target) throws Exception {
		VariableBinding vt1OutletTemperature = query(snmp, target, temperatureOid, PDU.GET);
		if (vt1OutletTemperature == null) {
			LOGGER.warn("No such instance|object target:" + target.getAddress() + " type:" + PDU.GET + " OID:"
					+ temperatureOid);
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("A_logtype", this.sign.getSubType());
		map.put("host", this.sign.getHost());
		map.put("temperature", vt1OutletTemperature.getVariable().toString());
		StringBuffer buffer = new StringBuffer();
		buffer.append(vt1OutletTemperature.getVariable().toString());// 温度
		map.put("A_message", buffer.toString());
		map.putAll(sign.getAttributes());
		Mushroom mushroom = new Mushroom();
		mushroom.setContent(map);
		putMushroom(sign, mushroom);
	}

	@Override
	protected void doConfigure(Context context) throws Exception {
		super.doConfigure(context);
		temperatureOid = sign.getOids().get(0);
	}

	@Override
	protected void doSend() throws Exception {
		temperature(this.snmp, this.target);
	}
}
