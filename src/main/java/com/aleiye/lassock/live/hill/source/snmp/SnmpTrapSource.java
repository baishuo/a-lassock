package com.aleiye.lassock.live.hill.source.snmp;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseConst;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.hill.source.AbstractEventDrivenSource;
import com.aleiye.lassock.live.model.GeneralMushroom;

/**
 * SNMP trap 采集子源
 * 
 * @author ruibing.zhao
 * @since 2015年6月8日
 * @version 2.1.2
 */
public class SnmpTrapSource extends AbstractEventDrivenSource implements CommandResponder {
	private static final Logger LOGGER = Logger.getLogger(SnmpTrapSource.class);

	// 协议
	private String protocol;
	// 端口
	private int port;

	// SNMP
	protected Snmp snmp;

	@Override
	protected void doConfigure(Course context) throws Exception {
		this.protocol = sign.getCourse().getString("protocol", CourseConst.PROTOCOL_UDP);
		this.port = sign.getCourse().getInteger("port", 161);
	}

	@Override
	protected void doStart() throws Exception {
		LOGGER.info("SNMPTrap " + port + " shade starting...");
		ThreadPool threadPool = ThreadPool.create("SNMP_TRAP", 2);
		MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
				new MessageDispatcherImpl());
		// 监听本地IP端口
		Address listenAddress = GenericAddress.parse(protocol + ":" + Sistem.getIp() + "/" + port);
		TransportMapping transport;
		// 对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);

		// 添加支持版本
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();
		snmp.addCommandResponder(this);
		LOGGER.info("SNMPTrap " + port + " shade started!");
	}

	@Override
	protected void doStop() throws Exception {
		if (snmp != null) {
			snmp.close();
		}
	}

	/** 
	 * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息 
	 * 当接收到trap时，会自动进入这个方法 
	 *  
	 * @param respEvnt 
	 */
	@Override
	public void processPdu(CommandResponderEvent respEvnt) {
		synchronized (this) {
			// 解析Response
			if (respEvnt != null && respEvnt.getPDU() != null) {
				// 获取远程地址
				Address peerAddress = respEvnt.getPeerAddress();
				String peerIP = "";
				if (peerAddress != null)
					peerIP = peerAddress.toString().split("/")[0];
				// 获取接收的OID信息
				@SuppressWarnings("unchecked")
				Vector<? extends VariableBinding> recVBs = respEvnt.getPDU().getVariableBindings();
				// 循环OID
				for (int i = 0; i < recVBs.size(); i++) {
					GeneralMushroom mr = new GeneralMushroom();
					VariableBinding recVB = recVBs.elementAt(i);
					StringBuffer sb = new StringBuffer();
					sb.append(System.currentTimeMillis());
					sb.append(" ");
					sb.append(Sistem.getHost());
					sb.append(" ");
					sb.append(peerIP);
					sb.append(" ");
					sb.append(recVB.getOid().toString());
					sb.append(" ");
					sb.append(recVB.getVariable().toString());
					mr.setBody(sb.toString().getBytes());
					try {
						putMushroom(mr);
					} catch (Exception e) {
						;
					}
				}
			}
		}
	}
}
