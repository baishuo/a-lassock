package com.aleiye.lassock.live.bazaar;

import com.aleiye.lassock.common.NamedComponent;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.basket.Basket;

/**
 * 采集消费者(集市)
 * 
 * @author ruibing.zhao
 * @since 2015年8月26日
 * @version 2.1.2
 */
public interface Bazaar extends LifecycleAware, NamedComponent {
	/**
	 * 设置消费对列
	 * @param basket 消费对列
	 */
	public void setBasket(Basket basket);

	/**
	 * 
	 * @return 消费对列
	 */
	public Basket getBasket();

	/**
	 * 消费处理
	 * @throws Exception
	 */
	public void process() throws Exception;

}
