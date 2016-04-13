package com.aleiye.lassock.live.station;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.conf.LiveConfiguration;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.basket.BasketFactory;
import com.aleiye.lassock.live.basket.DefaultBasketFactory;
import com.aleiye.lassock.live.basket.MemoryQueueBasket;

/**
 * 队列加载管理站
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public class BasketStation implements InitializeAware {
	// 配置信息
	LiveConfiguration configuration;

	/** 队列*/
	private Map<String, Basket> baskets;

	public BasketStation(LiveConfiguration configuration) throws Exception {
		this.configuration = configuration;
	}

	@Override
	public void initialize() throws Exception {
		load(configuration);
	}

	/**
	 * 加载队列
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	private void load(LiveConfiguration configuration) throws Exception {
		// 初始化队列栈
		baskets = new HashMap<String, Basket>();
		// 生成默认队列
		Basket defualt = new MemoryQueueBasket();
		defualt.setName("_DEFAULT");
		defualt.configure(new Context());
		defualt.start();
		baskets.put(defualt.getName(), defualt);

		BasketFactory factory = new DefaultBasketFactory();
		// 加载自定义扩展采集源
		Set<String> basketNames = configuration.getBasketSet();
		Map<String, Context> basketContexts = configuration.getBasketContextMap();
		for (String name : basketNames) {
			Context context = basketContexts.get(name);
			String strClass = context.getString("class");
			Basket basket = factory.create(name, strClass);
			basket.configure(context);
			basket.start();
			baskets.put(name, basket);
		}
	}

	public boolean contains(String key) {
		try {
			return baskets.containsKey(key);
		} catch (Exception e) {}
		return false;
	}

	public Basket getBasket(String name) {
		return baskets.get(name);
	}

	@Override
	public void destroy() {
		// 对列关闭
		for (LifecycleAware bk : baskets.values()) {
			bk.stop();
		}
		baskets.clear();
	}
}
