package com.aleiye.raker.ping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PingTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// String[] iplist = { "10.0.1.1", "10.0.1.2", "10.0.1.3", "10.0.1.4", "10.0.1.5", "10.0.1.6", "10.0.1.7",
		// "10.0.1.8", "10.0.1.9", "10.0.1.10", "10.0.1.11", "10.0.1.12", "10.0.1.13", "10.0.1.14", "10.0.1.15",
		// "10.0.1.16", "10.0.1.17", "10.0.1.18", "10.0.1.19", "10.0.1.20", "10.0.1.21", "10.0.1.22", "10.0.1.23",
		// "10.0.1.24", "10.0.1.25" };
		String[] iplist = { "10.0.1.37", "10.0.138", "10.0.100" };
		// 读取txt文件中的IP列表
		// Pinger pinger = new ExecPinger();
		// pinger.setParser(new DefaultPingParser());
		// List<String> iplist = pinger.getIpListFromTxt("d:/test/idc_ping_ip.txt");

		JumpPinger pinger = new JumpPinger();
		// pinger.setParser(new DefaultPingParser());
		// String[] ju = { "10.0.1.1;admin;yhxt@123" };
		// pinger.connect(ju);
		// for (String s : iplist) {
		// int count = pinger.ping(s);
		// if (count == pinger.getMaxPingCount()) {
		// System.out.println(s + " ping sucess!");
		// }
		// }
		// pinger.distinct();
		String s = "Success rate is 100 percent (5/5), round-trip min/avg/max = 1/1/4 ms";
		System.out.println(s.substring(s.indexOf("(") + 1, s.lastIndexOf(")") ));
		// ThreadPoolExecutor executorPool = new ThreadPoolExecutor(50, 60, 60, TimeUnit.SECONDS,
		// new ArrayBlockingQueue<Runnable>(50), new ThreadPoolExecutor.CallerRunsPolicy());
		// long startTime = System.currentTimeMillis();
		// final int maxCount = 4;
		// for (final String ip : iplist) {
		// executorPool.execute(new Runnable() {
		// public void run() {
		// PingTest pinger = new PingTest();
		// Integer countSucce = pinger.doPingCmd(ip, maxCount);
		// if (null != countSucce) {
		// System.out.println("host:[ " + ip + " ] ping cout: " + maxCount + " success: " + countSucce);
		// } else {
		// System.out.println("host:[ " + ip + " ] ping cout null");
		// }
		// }
		//
		// });
		// }

	}

	/**
	 * @param filepath
	 * @return list
	 */
	public List<String> getIpListFromTxt(String filepath) {
		BufferedReader br = null;
		List<String> iplist = new ArrayList<String>();
		try {
			File file = new File(filepath);
			br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				String line = br.readLine();
				if (null != line && !"".equals(line)) {
					iplist.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);

		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
				}
			}
		}
		return iplist;
	}

}