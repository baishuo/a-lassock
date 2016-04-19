package com.aleiye.lassock.live.hill.source.text1;

import java.io.IOException;

import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.hill.source.text1.cluser.TextCluser;

/**
 * 采集策略接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月25日
 * @version 2.1.2
 */
public interface PickPolicy {
	void pick(TextCluser shade) throws IOException, SignRemovedException, InterruptedException;
}
