package com.aleiye.raker.live.hill;

import java.util.Map;

import com.aleiye.raker.common.able.Destroyable;
import com.aleiye.raker.live.Live;
import com.aleiye.raker.live.basket.Basket;

/**
 * 采集源接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public interface Hill extends Live, Destroyable {

	void initialize() throws Exception;

	public void setBaskets(Map<String, Basket> baskets);

}
