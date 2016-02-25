package com.aleiye.lassock.test.telnet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.aleiye.lassock.live.hill.shade.telnet.ApacheTelnet;

public class Telnet2Test {
	public static void main(String[] args) {
		ApacheTelnet telnet = new ApacheTelnet("ANSI"); // Windows,用VT220,否则会乱码
		try {
			telnet.connect("10.160.2.251", 23);
			System.out.println(telnet.getPrew());
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String result = null;
//			while (true) {
//				String command = in.readLine().trim();
//				if (command.equals("quit")) {
//					break;
//				}
//				if (command.startsWith("!")) {
//					result = telnet.sendCommand(command.substring(1));
//				} else {
//					result = telnet.sendCommandToEnd(command);
//				}
//			}
			result = telnet.sendCommand("cisco");
			// System.err.println(result);
			result = telnet.sendCommand("cisco");
			System.err.println(result);
			result = telnet.sendCommand("en");
			// System.err.println(result);
			result = telnet.sendCommand("dc.cisco");
			// System.err.println(result);
			result = telnet.sendCommandToEnd("show run");
			// System.err.println(result);
			result = telnet.sendCommandToEnd("show arp");
			System.err.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			telnet.distinct();
		}
	}
}
