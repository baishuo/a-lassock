package com.aleiye.lassock.live.hill.source.snmp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.aleiye.event.constants.EventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;

public class SnmpFlowSource extends SnmpStandardSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnmpFlowSource.class);

	// portIpMap
	private String prefixOid;
	private String suffixOid;

	// inflow
	private String prefixInFlowOid;
	private String suffixInFlowOid;

	// outflow
	private String prefixOutFlowOid;
	private String suffixOutFlowOid;

	// 上一次结果
	private Map<String, Long> tempOidval = new HashMap<String, Long>();

	/**
	 * 获取port-ip映射
	 *
	 * @param snmp
	 * @param target
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> portIpMap(Snmp snmp, Target target) throws Exception {
		Map<String, String> map = query(snmp, target, prefixOid, suffixOid);
		Map<String, String> result = new HashMap<String, String>(map.size());
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			result.put(entry.getValue(), entry.getKey().replace(prefixOid + ".", ""));
		}
		return result;
	}

	/**
	 * 流量采集
	 *
	 * @param snmp
	 * @param target
	 * @param deviceIp 设备IP
	 * @throws Exception
	 */
	private void flow(Snmp snmp, Target target) throws Exception {
		// 接口收到的字节数
		Map<String, String> inIns = query(snmp, target, prefixInFlowOid, this.suffixInFlowOid);
		if (inIns.size() == 0) {
			LOGGER.warn("No such instances|objects target:" + target.getAddress() + " type:" + PDU.GETBULK + " OID:"
					+ prefixInFlowOid + "-" + suffixInFlowOid);
			return;
		}
		Map<String, FlowData> inMap = tidyFlowData(inIns);

		// 查找端口-IP映射
		Map<String, String> portIpMap = portIpMap(snmp, target);

		// 接口发送的字节数
		Map<String, FlowData> outMap = tidyFlowData(query(snmp, target, this.prefixOutFlowOid, this.suffixOutFlowOid));

		Iterator<Entry<String, FlowData>> iterator = inMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, FlowData> entry = iterator.next();
			String port = entry.getKey();
			FlowData flowData = entry.getValue();
			FlowData outFlowData = outMap.get(port);
			String linkip = portIpMap.get(port);
			// 日志内容(时间戳，设备IP，端口号，LINKIP，当前OID，每秒速率in，分钟总和in，当前流量in，上一次流量in，当前OID，每秒速率out，分钟总和out，当前流量out，上一次流量out)
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("port", flowData.getPort());
			map.put("linkIP", linkip);

			map.put("inSecSpeed", flowData.getSecSpeed());
			map.put("inPeriodFlow", flowData.getPeriodFlow());
			map.put("inFlow", flowData.getFlow());
			map.put("inLastFlow", flowData.getLastFlow());

			map.put("outSecSpeed", outFlowData.getSecSpeed());
			map.put("outPeriodFlow", outFlowData.getPeriodFlow());
			map.put("outFlow", outFlowData.getFlow());
			map.put("outLastFlow", outFlowData.getLastFlow());
			map.put(EventKey.DATA_TYPE_NAME, "a_flow");
			Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
			generalMushroom.getHeaders().put("target", this.param.getHost());
			putMushroom(generalMushroom);
		}
	}

	/**
	 * 将(oid->value)整理为(端口 -> 当前OID，每秒速率，分钟总和，当前流量，上一次流量)
	 *
	 * @param map key:端口，value:当前OID，每秒速率，分钟总和，当前流量，上一次流量
	 * @return
	 */
	private Map<String, FlowData> tidyFlowData(Map<String, String> map) {
		Map<String, FlowData> result = new HashMap<String, FlowData>(map.size());
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			String oid = entry.getKey();
			if (entry.getValue().matches("\\d+")) {
				long nowFlowVal = Long.parseLong(entry.getValue()) * 8;// 当前流量值
				Long lastFlowVal = tempOidval.get(oid);// 前一分钟流量值
				tempOidval.put(oid, nowFlowVal);
				if (lastFlowVal != null) {
					String[] arr = oid.split("\\.");
					String portStr = arr[arr.length - 1];// 端口号
					long value = calFlow(nowFlowVal, lastFlowVal);// 分钟总和
					long perValue = value / 60;// 每秒速率
					FlowData fd = new FlowData();
					fd.setOid(oid);
					fd.setPort(Integer.parseInt(portStr));
					fd.setFlow(nowFlowVal / (double) 1000);
					fd.setLastFlow(lastFlowVal / (double) 1000);
					fd.setPeriodFlow(value / (double) 1000);
					fd.setSecSpeed(perValue / (double) 1000);
					result.put(portStr, fd);
				}
			}
		}
		return result;
	}

	/**
	 * 流量信息
	 * 
	 * @author ruibing.zhao
	 * @since 2015年8月28日
	 * @version 2.1.2
	 */
	public static class FlowData {
		private String oid;
		private int port;
		// 周期流量
		private double periodFlow;
		// 每秒速率
		private double secSpeed;
		// 流量
		private double flow;
		// 上次注量
		private double lastFlow;

		public String getOid() {
			return oid;
		}

		public void setOid(String oid) {
			this.oid = oid;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public double getPeriodFlow() {
			return periodFlow;
		}

		public void setPeriodFlow(double periodFlow) {
			this.periodFlow = periodFlow;
		}

		public double getSecSpeed() {
			return secSpeed;
		}

		public void setSecSpeed(double secSpeed) {
			this.secSpeed = secSpeed;
		}

		public double getFlow() {
			return flow;
		}

		public void setFlow(double flow) {
			this.flow = flow;
		}

		public double getLastFlow() {
			return lastFlow;
		}

		public void setLastFlow(double lastFlow) {
			this.lastFlow = lastFlow;
		}

	}

	/**
	 * 根据当前采集到的流量值和前1分钟采集到的流量值计算这一分钟的流量
	 *
	 * @param nowVal
	 * @param lastVal
	 * @return
	 */
	private long calFlow(long nowVal, long lastVal) {
		long value = nowVal - lastVal;
		if (value < 0) {
			value = 4294967296l * 8 - lastVal + nowVal;
		}
		return value;
	}

	@Override
	protected void doConfigure(Course context) throws Exception {
		super.doConfigure(context);
		prefixOid = param.getOids().get(0);
		suffixOid = param.getOids().get(1);
		prefixInFlowOid = param.getOids().get(2);
		suffixInFlowOid = param.getOids().get(3);
		prefixOutFlowOid = param.getOids().get(4);
		suffixOutFlowOid = param.getOids().get(5);
	}

	@Override
	protected void doSend() throws Exception {
		flow(this.snmp, this.target);
	}
}
