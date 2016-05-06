package com.aleiye.lassock.live.station;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(BasketStation.class);

	private static final String DEFAULT_BASKET_NAME = "_DEFAULT";
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
		defualt.setName(DEFAULT_BASKET_NAME);
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
		return baskets.containsKey(key);
	}

	public Basket getBasket(String name) {
		String bkn = name;
		if (StringUtils.isBlank(bkn)) {
			logger.warn("Basket name is empty.Try to use the default basket.");
			bkn = DEFAULT_BASKET_NAME;
		} else if (!contains(bkn)) {
			logger.error("Basket can not find for name " + bkn + ".");
			throw new IllegalArgumentException("Basket can not find for name " + bkn + ".");
		}
		return baskets.get(DEFAULT_BASKET_NAME);
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
