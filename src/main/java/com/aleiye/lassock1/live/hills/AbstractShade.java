package com.aleiye.lassock1.live.hills;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.scroll.Sign;
import com.aleiye.lassock.model.Mushroom;

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

	/**
	 * 初始化必须绑定标识 和 输出对列
	 * 
	 * @param sign
	 * @param basket
	 */
	public AbstractShade(T sign, Basket basket) {
		this.sign = sign;
		this.basket = basket;
	}

	/**
	 * 存入采集产出(蘑茹放入竹篮)
	 * 
	 * @param mushroom
	 * @throws InterruptedException 线程阻塞唤醒异常,该异常发生时代表该Shade关闭
	 * @throws SignRemovedException Sign关联课程表为0时该异常发生,已无关联课程表的Sign(Shade)处于移除状态
	 */
	protected void putMushroom(Mushroom mushroom) throws InterruptedException, SignRemovedException {
		// 判断关联课程是否为空
		mushroom.setEnconde(sign.getEncoding());
		mushroom.putAll(sign.getValues());
		basket.push(mushroom);
		// MonitorHelper.setPicked(sign.getKey(), cid.split(","),
		// sign.getType(), sign.getDescription(),
		// mushroom.getContent().length);
	}

	@Override
	public boolean isOpen() {
		return done.get();
	}

	public T getSign() {
		return sign;
	}
}
