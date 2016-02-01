package com.aleiye.lassock.ping2;

import java.util.List;

import com.aleiye.lassock.ping2.Pinger.PingResult;

public class CiscoParser implements PingParser {

	@Override
	public PingResult parse(List<String> response) {
		// Sending 5, 100-byte ICMP Echos to 10.23.12.2, timeout is 2 seconds:
		// .....
		// Success rate is 0 percent (0/5)

		// Type escape sequence to abort.
		// Sending 5, 100-byte ICMP Echos to 10.10.255.129, timeout is 2 seconds:
		// !!!!!
		// Success rate is 100 percent (5/5), round-trip min/avg/max = 1/1/4 ms
		for (String s : response) {
			if (s.startsWith("Success")) {
				String ss = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
				String[] se = ss.split("/");
				if (se.length == 2) {
					int succ = Integer.parseInt(se[0]);
					int send = Integer.parseInt(se[1]);
					if (succ == send) {
						return PingResult.SUCCESS;
					} else if (succ == 0) {
						return PingResult.FAILED;
					} else {
						return PingResult.LOSS;
					}
				}
			}
		}
		return PingResult.FAILED;
	}
}
