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
 * CPU 使用获取
 * 
 * @author ruibing.zhao
 * @since 2015年8月30日
 * @version 2.1.2
 */
public class SnmpCpuSource extends SnmpStandardSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnmpCpuSource.class);

	private String cpuOid;

	// cpu
	public void cpu(Snmp snmp, Target target) throws Exception {
		VariableBinding cpmCPUTotal1min = query(snmp, target, cpuOid, PDU.GET);
		if (cpmCPUTotal1min == null) {
			LOGGER.warn("No such instance|object target:" + target.getAddress() + " type:" + PDU.GET + " OID:" + cpuOid);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cpuLoad", cpmCPUTotal1min.getVariable().toString());
		map.put(EventKey.DATA_TYPE_NAME , "cpu");
		// body
		Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
		generalMushroom.getHeaders().put("target", this.param.getHost());
		putMushroom(generalMushroom);
	}

	@Override
	protected void doConfigure(Course context) throws Exception {
		super.doConfigure(context);
		cpuOid = this.param.getOids().get(0);
	}

	@Override
	protected void doSend() throws Exception {
		cpu(this.snmp, this.target);
	}
}
