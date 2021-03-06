package com.aleiye.lassock.live.hill.source.snmp;

import java.util.HashMap;
import java.util.Map;

import com.aleiye.event.constants.EventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;

/**
 * SNMP 温度采集
 * 
 * @author ruibing.zhao
 * @since 2015年8月30日
 * @version 2.1.2
 */
public class SnmpTemperatureSource extends SnmpStandardSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnmpTemperatureSource.class);

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
		map.put("host", this.param.getHost());
		map.put("temperature", vt1OutletTemperature.getVariable().toString());
		map.put(EventKey.DATA_TYPE_NAME , "temperature");
		Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
		generalMushroom.getHeaders().put("target", this.param.getHost());
		generalMushroom.getHeaders().put("target", this.param.getHost());
		putMushroom(generalMushroom);
	}

	@Override
	protected void doConfigure(Course context) throws Exception {
		super.doConfigure(context);
		temperatureOid = param.getOids().get(0);
	}

	@Override
	protected void doSend() throws Exception {
		temperature(this.snmp, this.target);
	}
}
