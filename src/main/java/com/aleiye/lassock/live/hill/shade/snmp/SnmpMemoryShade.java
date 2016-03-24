package com.aleiye.lassock.live.hill.shade.snmp;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;

/**
 * MEMORY
 * 
 * @author ruibing.zhao
 * @since 2015年8月30日
 * @version 2.1.2
 */
public class SnmpMemoryShade extends SnmpStandardShade {
	private static final Logger LOGGER = Logger.getLogger(SnmpMemoryShade.class);
	// 是否总量
	boolean totaled = false;
	// 使用值
	String usedOid;
	// 伴随值
	String withOid;

	DecimalFormat df = new DecimalFormat(".##");

	@Override
	protected void doConfigure(Course context) throws Exception {
		super.doConfigure(context);
		usedOid = this.sign.getOids().get(0);
		withOid = this.sign.getOids().get(1);
		if (this.sign.containsKey("total"))
			totaled = this.sign.getBoolean("total");
	}

	// 内存
	public void memory(Snmp snmp, Target target) throws Exception {
		VariableBinding usedVariable = query(snmp, target, usedOid, PDU.GET);
		VariableBinding withVariable = query(snmp, target, withOid, PDU.GET);
		if (usedVariable == null || withVariable == null) {
			LOGGER.warn("No such instance|object target:" + target.getAddress() + " type:" + PDU.GET + " OID:"
					+ usedOid + "|" + withOid);
			throw new Exception("Can't read value with " + usedOid + " or " + withOid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		long memoryUsed = usedVariable.getVariable().toLong();
		map.put("memoryUsed", memoryUsed);
		long memoryFree = 0, memoryTotal = 0;
		if (totaled) {
			memoryTotal = withVariable.getVariable().toLong();
			memoryFree = memoryTotal - memoryUsed;
		} else {
			memoryFree = withVariable.getVariable().toLong();
			memoryTotal = memoryUsed + memoryFree;
		}
		map.put("memoryTotal", memoryTotal);
		map.put("memoryFree", memoryFree);
		double memoryFreeRate = (double) memoryFree / memoryTotal * 100;
		Double mfr = Double.valueOf(df.format(memoryFreeRate));
		map.put("memoryFreeRate", mfr);
		// 附加属性添加到采集
		Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
		generalMushroom.getHeaders().put("target", this.sign.getHost());
		putMushroom(sign, generalMushroom);
	}

	@Override
	protected void doSend() throws Exception {
		memory(snmp, target);
	}
}
