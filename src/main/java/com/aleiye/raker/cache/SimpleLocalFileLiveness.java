package com.aleiye.raker.cache;

import com.aleiye.raker.live.Live;
import com.aleiye.raker.live.scroll.Const;
import com.aleiye.raker.live.scroll.Course;

public class SimpleLocalFileLiveness implements Liveness {

	@Override
	public void initialize() throws Exception {

	}

	@Override
	public void lisen(final Live live) throws Exception {
		try {
			// final Course text = new Course();
			// // text
			// text.setId("100");
			// text.setType(0);
			// text.setTypeName("text");
			// text.put(Const.text.DATA_INPUT_PATH,
			// "/Users/asuroslove/Documents/aa.txt");
			// // s.put(Const.text.DATA_INPUT_PATH,
			// // "/Users/asuroslove/ide/apache-tomcat-7.0.57/logs");
			// text.put(Const.text.PATH_FILTER_REGEX, ".*\\.log");
			// text.put(Const.text.CHANGED_READ_COUNT, 0);
			// text.put(Const.text.MOVE_PATH,
			// "/Users/asuroslove/Documents");
			// syslog
			final Course syslog = new Course();
			syslog.setId("101");
			syslog.setName("1");
			syslog.setType("syslog");
			syslog.setBasketName("simple");
			syslog.setSubType("udp");
			syslog.put(Const.syslog.PROTOCOL, Const.PROTOCOL_UDP);
			syslog.put(Const.syslog.PORT, "5143");
			live.add(syslog);
			// snmp
			//			final Course snmp = new Course();
			//			snmp.setId("102");
			//			snmp.setName("snmptest");
			//			snmp.setType("snmp");
			//			snmp.setSubType("cpu");
			//			snmp.put(Const.snmp.HOST, "aleiyec");
			//			snmp.put(Const.snmp.PORT, "161");
			//			snmp.put(Const.snmp.PROTOCOL, Const.PROTOCOL_UDP);
			//			snmp.put(Const.snmp.VERSION, "1");
			//			snmp.put("devType", "cisco");
			//			snmp.setRunType(RunType.TIMER);
			//			snmp.setBasketName("simple");
			//			List<String> oids = new ArrayList<String>();
			//			// oids.add(".1.3.6.1.2.1.1.1");
			//			oids.add("1.3.6.1.4.1.2021.10.1.3.1");
			//			//
			//			snmp.put(Const.snmp.OIDS, oids);
			//			live.add(snmp);
			//
			// // snmptrap
			// final Course snmptrap = new Course();
			// snmptrap.setId("103");
			// snmptrap.setType(3);
			// snmptrap.setTypeName("snmp");
			// snmptrap.put(Const.snmp.IS_TRAP, "true");
			//
			// snmptrap.put(Const.snmp.PROTOCOL, Const.PROTOCOL_UDP);
			// snmptrap.put(Const.snmp.PORT, "1611");
			//
			// final Course command = new Course();
			// command.setId("101");
			// command.setName("tlllll");
			// command.setType("telnet");
			// command.put(Const.command.HOST, "10.0.1.1");
			// command.put(Const.command.PORT, "23");
			// command.put(Const.command.UESRNAME, "admin");
			// command.put(Const.command.PASSWORD, "yhxt@123");
			// String[] commands = {
			// "dis arp", "dis mac-address"
			// };
			// command.setBasketName("simple");
			// command.setRunType(RunType.CRON);
			// command.setCron("0 */5 * ? * *");
			// command.put(Const.command.COMMANDS, commands);
			// live.add(command);
			//
			//			final Course jdbc = new Course();
			//			jdbc.setId("102");
			//			jdbc.setName("resourinfo");
			//			jdbc.setType("jdbc");
			//			jdbc.setBasketName("simple");
			//			jdbc.setRunType(RunType.TIMER);
			//			jdbc.put(Const.jdbc.DATA_SOURCE_NAME, "collect");
			//			jdbc.put(Const.jdbc.SQL, "select * from RESOURCE_INFO");
			//			jdbc.setPeriod(15000);
			//			live.add(jdbc);
			// // live.add(text);
			// live.add(snmp);
			// live.add(snmptrap);

			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(600000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						//						live.remove(command);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {

	}
}
