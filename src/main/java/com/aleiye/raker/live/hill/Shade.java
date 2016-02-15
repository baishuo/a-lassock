package com.aleiye.raker.live.hill;

import com.aleiye.raker.live.NamedComponent;
import com.aleiye.raker.live.basket.Basket;
import com.aleiye.raker.live.lifecycle.LifecycleAware;
import com.aleiye.raker.live.scroll.Sign;

/**
 * 采集子源
 * (子源，它存在于<tt>Course</tt> 配置生成的容器[<tt>Hill</tt>]中)
 * <br>
 * 一个采集源包含多个子源 
 *    如:文件采集,一个目录有多个文件可以采集
 * 
 * <p>树阴，蘑茹长在树阴下</p>
 * 
 * @author ruibing.zhao
 * @since 2015年5月14日
 * @version 2.1.2
 * @see Hill
 */
public interface Shade extends LifecycleAware, NamedComponent {

	public void setBasket(Basket basket);

	public Basket getBasket();

	public Sign getSign();
}
