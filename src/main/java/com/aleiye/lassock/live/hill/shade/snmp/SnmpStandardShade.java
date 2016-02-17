package com.aleiye.lassock.live.hill.shade.snmp;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.live.scroll.Const;
import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.util.ScrollUtils;

/**
 * SNMP 主动请求
 * @author ruibing.zhao
 * @since 2015年8月28日
 * @version 2.1.2
 */
public class SnmpStandardShade extends SnmpShade {
	private static final String ADDRESS_FORMAT = "%s:%s/%d";
	// 通信超时
	private static final long timeout = 15000;
	// SNMP send不成功重复次数
	private static int retries = 3;

	/** 设置管理进程的IP和端口 */
	TransportMapping transport = null;

	// 生成目标地址对象
	private Address targetAddress;
	// 采集目标
	protected Target target;

	// SNMP版本
	protected int version = 1;
	// 同步异步
	private boolean syn = false;

	private boolean keeped = false;

	/**
	 * SNMP 响应处理
	 * @param event
	 */
	protected void responceHandle(ResponseEvent event) {
		// 解析Response
		if (event != null && event.getResponse() != null) {
			// 获取接收的OID信息
			@SuppressWarnings("unchecked")
			Vector<? extends VariableBinding> recVBs = event.getResponse().getVariableBindings();
			// 循环OID
			for (int i = 0; i < recVBs.size(); i++) {
				Mushroom mr = new Mushroom();
				VariableBinding recVB = recVBs.elementAt(i);
				StringBuffer sb = new StringBuffer();
				sb.append(System.currentTimeMillis());
				sb.append(" ");
				sb.append(Sistem.getHost());
				sb.append(" ");
				sb.append(this.sign.getHost());
				sb.append(" ");
				sb.append(recVB.getOid().toString());
				sb.append(" ");
				sb.append(recVB.getVariable().toString());
				byte[] baos = sb.toString().getBytes();
				mr.setContent(baos);
				try {
					putMushroom(sign, mr);
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	@Override
	protected void doConfigure(Context context) throws Exception {
		sign = (SnmpSign) ScrollUtils.forSign(context, SnmpSign.class);
		// 目标地址
		targetAddress = GenericAddress.parse(String.format(ADDRESS_FORMAT, sign.getProtocol(), sign.getHost(),
				sign.getPort()));
		// 版本
		version = sign.getVersion();

		// SNMP 是否保存连接状态
		// 默认状态保持
		// if (sign.getRunType() == RunType.DEFAULT) {
		// keeped = true;
		// } else {
		// // 周期执行时间小于30秒
		// if (sign.getRunType() == RunType.TIMER && sign.getPeriod() < 30000) {
		// keeped = true;
		// }
		// }
	}

	@Override
	protected void doStart() throws Exception {
		;
	}

	protected void openListen() throws Exception {
		if (Const.PROTOCOL_TCP.equals(this.sign.getProtocol().toLowerCase())) {
			transport = new DefaultTcpTransportMapping();
		} else {
			transport = new DefaultUdpTransportMapping();
		}
		snmp = new Snmp(transport);
		if (version == SnmpConstants.version3) {
			// 设置安全模式
			USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
			SecurityModels.getInstance().addSecurityModel(usm);
		}
		// 开始监听消息
		transport.listen();

		/** 创建目标 */
		if (version == SnmpConstants.version3) {
			// 添加用户
			snmp.getUSM().addUser(
					new OctetString(this.sign.getUsrName()),
					new UsmUser(new OctetString(this.sign.getUsrName()), AuthMD5.ID, new OctetString(this.sign
							.getPassword()), PrivDES.ID, new OctetString(this.sign.getPassword())));
			target = new UserTarget();
			// 设置安全级别
			((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
			((UserTarget) target).setSecurityName(new OctetString(this.sign.getUsrName()));
			((UserTarget) target).setVersion(SnmpConstants.version3);
		} else {
			target = new CommunityTarget();
			if (version == SnmpConstants.version1) {
				target.setVersion(SnmpConstants.version1);
			} else {
				target.setVersion(SnmpConstants.version2c);
			}
			String community = "public";
			if (StringUtils.isNotBlank(this.getSign().getCommunity())) {
				community = this.getSign().getCommunity();
			}
			((CommunityTarget) target).setCommunity(new OctetString(community));
		}
		// 目标对象相关设置
		target.setAddress(targetAddress);
		// 采集超时
		// target.setTimeout(timeout);
		// 超时重试次数
		target.setRetries(retries);

		// if (keeped) {
		// snmpListen();
		// }
	}

	@Override
	protected void doPick() throws Exception {
		try {
			openListen();
			// if (!keeped) {
			doSend();
			// snmpListen();
			// }
		} finally {
			stopListen();
		}
	}

	protected void doSend() throws Exception {
		PDU pdu = makePDU(this.sign.getOids(), PDU.GET);
		if (!syn) {
			// 发送报文 并且接受响应
			ResponseEvent event = snmp.send(pdu, target);
			responceHandle(event);
		} else {
			// 异步模式
			// 设置监听对象
			ResponseListener listener = new ResponseListener() {
				@Override
				public void onResponse(ResponseEvent event) {
					responceHandle(event);
				}
			};
			// 发送报文
			snmp.send(pdu, target, null, listener);
		}
	}

	private void stopListen() {
		if (this.transport != null) {
			try {
				this.transport.close();
				this.transport = null;
			} catch (IOException ioe) {
				// ignore
			}
		}

		if (this.snmp != null) {
			try {
				this.snmp.close();
				this.snmp = null;
			} catch (IOException ioe) {
				// ignore
			}
		}

	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (this.transport != null) {
			this.transport.close();
		}
	}

	// protected void snmpListen() throws Exception {
	// /** 设置管理进程的IP和端口 */
	// TransportMapping transport;
	// if (Const.PROTOCOL_TCP.equals(this.sign.getProtocol().toLowerCase())) {
	// transport = new DefaultTcpTransportMapping();
	// } else {
	// transport = new DefaultUdpTransportMapping();
	// }
	// snmp = new Snmp(transport);
	// if (version == SnmpConstants.version3) {
	// // 设置安全模式
	// USM usm = new USM(SecurityProtocols.getInstance(), new
	// OctetString(MPv3.createLocalEngineID()), 0);
	// SecurityModels.getInstance().addSecurityModel(usm);
	// }
	// // 开始监听消息
	// transport.listen();
	// }
}
