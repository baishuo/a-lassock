package com.aleiye.lassock.ping;

import java.util.List;

/**
 * 默认 ping 命令执行回显解析
 * 
 * @author ruibing.zhao
 * @since 2015年11月17日
 */
public class DefaultPingParser implements PingParser {

	@Override
	public int parse(List<String> response) {
		int countTrue = 0;
		/*
		 * 跳过第一行
		 */
		for (int i = 1; i < response.size(); i++) {
			String msg = response.get(i);
			if (msg.split("=").length == 5) {
				countTrue++;
			}
		}
		return countTrue;
	}

}
