package com.aleiye.lassock.live.hill.shade;

import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.live.NamedLifecycle;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.hill.Shade;
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.model.GeneralMushroom;
import com.aleiye.lassock.model.Mushroom;
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

	protected Intelligence intelligence;

	public AbstractShade() {
		this.intelligence = new Intelligence(this.name);
	}

	@Override
	public synchronized void start() {
		Preconditions.checkState(basket != null, "No basket processor configured");
		super.start();
	}

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param generalMushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 */
	protected void putMushroom(Sign sign, Mushroom generalMushroom) throws InterruptedException {
		// // 判断关联课程是否为空
		// String cid = sign.getCourseIds();
		// if (StringUtils.isBlank(cid)) {
		// // 无关联课程表的Sign(Shade)处于移除状态
		// throw new SignRemovedException("Shade:" + sign.getId() +
		// " was removed!");
		// }
		// generalMushroom.setEnconde(sign.getEncoding());
		// generalMushroom.setSignId(sign.getId());

		generalMushroom.setOriginalValues(sign.getValues());
		generalMushroom.getHeaders().put("type", sign.getType());
		generalMushroom.getHeaders().put("subType", sign.getSubType());
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
