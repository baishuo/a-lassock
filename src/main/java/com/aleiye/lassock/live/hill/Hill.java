package com.aleiye.lassock.live.hill;

import java.util.Map;

import com.aleiye.lassock.common.able.Destroyable;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.live.basket.Basket;

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
