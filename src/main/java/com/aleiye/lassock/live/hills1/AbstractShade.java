package com.aleiye.lassock.live.hills1;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.common.utils.EncrypDES;
import com.aleiye.event.constants.EventKey;
import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.model.GeneralMushroom;

/**
 * 抽像采集子源
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class AbstractShade<T extends Sign> implements Shade {

	// 标识头
	protected final T sign;

	// 传输缓存对列
	private final Basket basket;

	// 开关
	protected AtomicBoolean done = new AtomicBoolean(false);

	protected Intelligence intelligence;

	/**
	 * 初始化必须绑定标识 和 输出对列
	 * 
	 * @param sign
	 * @param basket
	 */
	public AbstractShade(T sign, Basket basket) {
		this.sign = sign;
		this.basket = basket;
		intelligence = new Intelligence(sign.getCourseIds());
	}

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param generalMushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 * @throws SignRemovedException Sign关联课程表为0时该异常发生,已无关联课程表的Sign(Shade)处于移除状态
	 */
	protected void putMushroom(GeneralMushroom generalMushroom) throws InterruptedException, SignRemovedException {
		// 判断关联课程是否为空
		generalMushroom.setOriginalValues(sign.getValues());
		if (generalMushroom.getIntelligence() == null) {
			generalMushroom.setIntelligence(this.intelligence);
		}
		((GeneralMushroom) generalMushroom).setIntelligence(this.intelligence);
		generalMushroom.getHeaders().put(EventKey.RESOURCEID, sign.getCourseIds());
		generalMushroom.getHeaders().put(EventKey.USERID,
				EncrypDES.decrypt(Sistem.getHeader().get("authkey").toString()));
		generalMushroom.getHeaders().put(EventKey.MAC, Sistem.getMac());
		generalMushroom.getHeaders().put(EventKey.HOSTNAME, Sistem.getHost());
		// 每次事件产生 增1
		basket.push(generalMushroom);
		this.intelligence.setAcceptedCount(this.intelligence.getAcceptedCount() + 1);
	}

	@Override
	public boolean isOpen() {
		return done.get();
	}

	public T getSign() {
		return sign;
	}

	@Override
	public Intelligence getIntelligence() {
		return intelligence;
	}
}
