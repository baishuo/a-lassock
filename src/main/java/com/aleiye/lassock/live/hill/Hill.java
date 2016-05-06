package com.aleiye.lassock.live.hill;

import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.live.station.BasketStation;

/**
 * 采集源接口
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 */
public interface Hill extends Live, InitializeAware {
	public void setBaskets(BasketStation baskets);
}
