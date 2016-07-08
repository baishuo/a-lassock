package com.aleiye.lassock.live.hill.source.snmp;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.aleiye.lassock.live.hill.source.AbstractEventTrackSource;

/**
 * SNMP
 * 
 * @author ruibing.zhao
 * @since 2015年6月9日
 * @version 2.1.2
 */
public abstract class SnmpSource extends AbstractEventTrackSource {
	// SNMP服务
	protected Snmp snmp;

	/**
	 * 查询[prefixOid, suffixOid)段的oid/value信息
	 *
	 * @param snmp
	 * @param target
	 * @param prefixOid
	 * @param suffixOid
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> query(Snmp snmp, Target target, String prefixOid, String suffixOid) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String oid = prefixOid;
		boolean flag = true;
		while (flag) {
			PDU request = new PDU();
			request.setType(PDU.GETBULK);
			request.setMaxRepetitions(10);
			request.add(new VariableBinding(new OID(oid)));
			ResponseEvent respEvt = snmp.send(request, target);
			if (respEvt != null && respEvt.getResponse() != null) {
				@SuppressWarnings("unchecked")
				Vector<? extends VariableBinding> revBindings = respEvt.getResponse().getVariableBindings();
				for (int i = 0; i < revBindings.size(); i++) {
					VariableBinding vbs = revBindings.elementAt(i);
					String coid = vbs.getOid().toString();
					if (StringUtils.isBlank(suffixOid) && !coid.contains(prefixOid)) {
						flag = false;// 跳出while循环的标志
						break;// 跳出for循环
					}
					if (coid.contains(suffixOid)) {
						flag = false;// 跳出while循环的标志
						break;// 跳出for循环
					}
					String value = vbs.getVariable().toString();
					if (value.equals("noSuchInstance") || value.equals("noSuchObject")) {
						// LOGGER.warn(sign.getHost() + " No such for OID:" +
						// oid);
						continue;
					}
					map.put(coid, value);
				}
				oid = revBindings.get(revBindings.size() - 1).getOid().toString();
			} else {
				flag = false;// 跳出while循环的标志
			}
		}
		return map;
	}

	/**
	 * 转换map，端口是key，值是value
	 * @param map
	 * @return
	 * @throws Exception
	 */
	protected static Map<String, String> mapSwitch(Map map) throws Exception {
		if(map.size() ==0 ){
			return null;
		}
		Map<String, String> result = new HashMap<String, String>(map.size());
		Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, String> entry = iterator.next();
			String[] arr = entry.getKey().split("\\.");
			String port = arr[arr.length - 1];
			result.put(port,entry.getValue());
		}
		return result;
	}

	/**
	 * 查询Oid信息
	 *
	 * @param snmp
	 * @param target
	 * @param oid
	 * @param type
	 * @return
	 * @throws Exception
	 */

	protected static VariableBinding query(Snmp snmp, Target target, String oid, int type) throws Exception {
		PDU request = new PDU();
		request.setType(type);
		request.setNonRepeaters(10);
		request.add(new VariableBinding(new OID(oid)));
		VariableBinding ret = null;
		ResponseEvent respEvt = snmp.send(request, target);
		if (respEvt != null && respEvt.getResponse() != null) {
			@SuppressWarnings("unchecked")
			Vector<? extends VariableBinding> revBindings = respEvt.getResponse().getVariableBindings();
			for (int i = 0; i < revBindings.size(); i++) {
				VariableBinding vbs = revBindings.elementAt(i);
				String value = vbs.getVariable().toString();
				if (value.equals("noSuchInstance") || value.equals("noSuchObject")) {
					continue;
				}
				ret = vbs;
				break;
			}
		}
		return ret;
	}

	protected Map<String, String> toMap(Vector<? extends VariableBinding> revBindings) {
		Map<String, String> ret = new HashMap<String, String>();
		for (int i = 0; i < revBindings.size(); i++) {
			VariableBinding vbs = revBindings.elementAt(i);
			String value = vbs.getVariable().toString();
			if (value.equals("noSuchInstance") || value.equals("noSuchObject")) {
				ret.put(vbs.getOid().toString(), "");
			} else {
				ret.put(vbs.getOid().toString(), value);
			}
		}
		return ret;
	}

	protected PDU makePDU(List<String> oids, int type) {
		/** PDU 创建 */
		PDU pdu = new PDU();
		for (String s : oids) {
			pdu.add(new VariableBinding(new OID(s)));
		}
		pdu.setNonRepeaters(10);
		pdu.setType(type);

		return pdu;
	}

	protected PDU makePDU(String oids, int type) {
		/** PDU 创建 */
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oids)));
		pdu.setNonRepeaters(10);
		pdu.setType(type);
		return pdu;
	}

	@Override
	protected void doStop() throws Exception {
		if (snmp != null)
			try {
				snmp.close();
				snmp = null;
			} catch (IOException ioe) {
				// ignore
			}
	}
}
