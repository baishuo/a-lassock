package com.aleiye.raker.live.hill.shade.text;

import java.io.IOException;

import com.aleiye.raker.live.exception.SignRemovedException;

/**
 * 采集策略接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月25日
 * @version 2.1.2
 */
public interface PickPolicy {
	void pick(TextShade shade) throws IOException, SignRemovedException, InterruptedException;
}
