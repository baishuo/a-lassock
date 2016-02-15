package com.aleiye.raker.ping2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * PING
 * 
 * @author ruibing.zhao
 * @since 2015年10月13日
 */
public class ExecPinger extends Pinger {
	private final Logger LOGGER = Logger.getLogger(ExecPinger.class);

	public PingResult ping(String ip) {
		LineNumberReader input = null;
		try {
			String pingCmd = String.format(this.getPattern(), ip);
			Process process = Runtime.getRuntime().exec(pingCmd);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			input = new LineNumberReader(ir);
			String line;
			List<String> response = new ArrayList<String>();
			while ((line = input.readLine()) != null) {
				LOGGER.debug(line);
				if (!"".equals(line)) {
					response.add(line);
				} else {
					break;
				}
			}
			return this.getParser().parse(response);
		} catch (IOException e) {
			LOGGER.error("Ping " + ip + " is exception!");
			LOGGER.debug("", e);
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException ex) {
					LOGGER.error("Close ping " + ip + " reader exception!");
					LOGGER.debug("", ex);
				}
			}
		}
		return PingResult.FAILED;
	}
}
