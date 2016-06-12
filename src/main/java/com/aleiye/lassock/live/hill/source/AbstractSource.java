package com.aleiye.lassock.live.hill.source;

import com.aleiye.common.utils.EncrypDES;
import com.aleiye.event.constants.EventKey;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.NamedLifecycle;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.model.GeneralMushroom;
import com.aleiye.lassock.live.model.Mushroom;
import com.google.common.base.Preconditions;

/**
 * 抽像采集
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class AbstractSource extends NamedLifecycle implements Source {
	// 采集标识
	protected Sign sign;
	// 传输缓存对列
	private Basket basket;

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket configured");
		Preconditions.checkState(sign != null, "No sign configured");
		super.start();
	}

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param generalMushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 */
	protected void putMushroom(Mushroom generalMushroom) throws InterruptedException {
		generalMushroom.getHeaders().put(EventKey.RESOURCEID, this.getName());
		generalMushroom.getHeaders().put(EventKey.USERID,
				EncrypDES.decrypt(Sistem.getHeader().get("authkey").toString()));
		generalMushroom.getHeaders().put(EventKey.MAC, Sistem.getMac());
		generalMushroom.getHeaders().put(EventKey.HOSTNAME, Sistem.getHost());

		generalMushroom.setOriginalValues(sign.getCourse().getValues());
//		generalMushroom.getHeaders().put("lassockDataType", sign.getCourse().getType().toString());
		GeneralMushroom mr = (GeneralMushroom) generalMushroom;
		if (mr.getIntelligence() == null) {
			mr.setIntelligence(sign.getIntelligence());
		}
		basket.push(generalMushroom);
		// 每次事件产生 增1
		sign.getIntelligence().setAcceptedCount(sign.getIntelligence().getAcceptedCount() + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Basket getBasket() {
		return basket;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Intelligence getIntelligence() {
		return this.getSign().getIntelligence();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sign getSign() {
		return sign;
	}
}
