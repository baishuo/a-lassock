package com.aleiye.lassock.live.station;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.conf.ConfigurationConstants;
import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.conf.LiveConfiguration;
import com.aleiye.lassock.lifecycle.LifecycleAware;
import com.aleiye.lassock.live.bazaar.Bazaar;
import com.aleiye.lassock.live.bazaar.BazaarFactory;
import com.aleiye.lassock.live.bazaar.BazaarRunner;
import com.aleiye.lassock.live.bazaar.DefaultBazaarFactory;

/**
 * 消费端加载管理站
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public class BazaarStation implements InitializeAware {
	// 配置信息
	private LiveConfiguration configuration;
	// 消费对列
	private BasketStation basketStation;
	// 消费
	private Map<String, BazaarRunner> bazaarRunners;

	public BazaarStation(LiveConfiguration configuration, BasketStation basketStation) {
		this.configuration = configuration;
		this.basketStation = basketStation;
		// 初始化队列栈
		bazaarRunners = new HashMap<String, BazaarRunner>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		BazaarFactory factory = new DefaultBazaarFactory();
		// 加载自定义扩展采集源
		Set<String> bazaarNames = configuration.getBazaarSet();
		Map<String, Context> bazaarContexts = configuration.getBazaarContextMap();
		for (String name : bazaarNames) {
			Context context = bazaarContexts.get(name);
			String clazz = context.getString("class");
			Bazaar bazaar = factory.create(name, clazz);
			String basketName = context.getString(ConfigurationConstants.CONFIG_BASKET);
			bazaar.setBasket(basketStation.getBasket(basketName));
			bazaar.configure(context);
			BazaarRunner runner = BazaarRunner.forBazaar(bazaar);
			runner.start();
			bazaarRunners.put(name, runner);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		// 集市关闭
		for (LifecycleAware br : bazaarRunners.values()) {
			br.stop();
		}
		bazaarRunners.clear();
	}
}
