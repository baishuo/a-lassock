package com.aleiye.lassock.live.hill.source.text.cluser;

import java.io.IOException;

/**
 * 采集策略接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月25日
 * @version 2.1.2
 */
public interface PickPolicy {
	void pick(Cluser shade) throws IOException, InterruptedException;
}
