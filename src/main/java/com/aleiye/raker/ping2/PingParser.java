package com.aleiye.raker.ping2;

import java.util.List;

import com.aleiye.raker.ping2.Pinger.PingResult;

/**
 * Ping 命令执行结果解析
 * 
 * @author ruibing.zhao
 * @since 2015年10月13日
 */
public interface PingParser {
	/**
	 * 解析 结果交返回ping 成功数
	 * 
	 * @param msg
	 * @return
	 */
	PingResult parse(List<String> reponse);
}
