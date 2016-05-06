package com.aleiye.lassock.live.basket;

/**
 * 设置队列
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public interface BasketAware {
	/**
	 *  设置通道
	 * @param basket
	 */
	public void setBasket(Basket basket);

	/**
	 *  获取通道
	 * @param basket
	 */
	public Basket getBasket();
}
