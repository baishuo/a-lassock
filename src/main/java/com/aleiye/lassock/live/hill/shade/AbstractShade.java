package com.aleiye.lassock.live.hill.shade;

import org.apache.commons.lang3.StringUtils;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.hill.Shade;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.aleiye.lassock.live.scroll.Sign;
import com.aleiye.lassock.model.Mushroom;
import com.google.common.base.Preconditions;

/**
 * 抽像采集子源
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class AbstractShade implements Shade {
	// 名称
	private String name;
	// 传输缓存对列
	private Basket basket;
	// 状态
	protected LifecycleState lifecycleState;

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param mushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 * @throws SignRemovedException Sign关联课程表为0时该异常发生,已无关联课程表的Sign(Shade)处于移除状态
	 */
	protected void putMushroom(Sign sign, Mushroom mushroom) throws InterruptedException, SignRemovedException {
		// 判断关联课程是否为空
		String cid = sign.getCourseIds();
		if (StringUtils.isBlank(cid)) {
			// 无关联课程表的Sign(Shade)处于移除状态
			throw new SignRemovedException("Shade:" + sign.getId() + " was removed!");
		}
		mushroom.setEnconde(sign.getEncoding());
		mushroom.setSignId(sign.getId());
		basket.push(mushroom);
	}

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket processor configured");
		lifecycleState = LifecycleState.START;
	}

	@Override
	public synchronized void stop() {
		lifecycleState = LifecycleState.STOP;
	}

	@Override
	public synchronized LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	@Override
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + "{name:" + name + ",state:" + lifecycleState + "}";
	}

	@Override
	public Basket getBasket() {
		return basket;
	}

	@Override
	public void setBasket(Basket basket) {
		this.basket = basket;
	}
}
