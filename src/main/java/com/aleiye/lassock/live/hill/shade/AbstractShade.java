package com.aleiye.lassock.live.hill.shade;

import com.aleiye.common.utils.EncrypDES;
import com.aleiye.event.constants.EventKey;
import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.NamedLifecycle;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.model.GeneralMushroom;
import com.aleiye.lassock.live.model.Mushroom;
import com.google.common.base.Preconditions;

/**
 * 抽像采集子源
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class AbstractShade extends NamedLifecycle implements Shade {
	// 传输缓存对列
	private Basket basket;
	// 运行情报
	protected Intelligence intelligence;
	// 课程配置
	protected Course course;

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket configured");
		super.start();
	}

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param generalMushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 */
	protected void putMushroom(Sign sign, Mushroom generalMushroom) throws InterruptedException {
		generalMushroom.getHeaders().put(EventKey.RESOURCEID, this.course.getName());
		generalMushroom.getHeaders().put(EventKey.USERID,
				EncrypDES.decrypt(Sistem.getHeader().get("authkey").toString()));
		generalMushroom.getHeaders().put(EventKey.MAC, Sistem.getMac());
		generalMushroom.getHeaders().put(EventKey.HOSTNAME, Sistem.getHost());

		generalMushroom.setOriginalValues(sign.getValues());
		generalMushroom.getHeaders().put("type", sign.getType());
		((GeneralMushroom) generalMushroom).setIntelligence(this.intelligence);
		basket.push(generalMushroom);
		// 每次事件产生 增1
		this.intelligence.setAcceptedCount(this.intelligence.getAcceptedCount() + 1);
	}

	@Override
	public Basket getBasket() {
		return basket;
	}

	@Override
	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	@Override
	public Intelligence getIntelligence() {
		return intelligence;
	}
}
