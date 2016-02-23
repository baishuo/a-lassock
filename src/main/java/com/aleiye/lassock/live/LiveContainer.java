package com.aleiye.lassock.live;

import java.util.HashMap;
import java.util.Map;

import com.aleiye.lassock.common.able.Destroyable;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.basket.BasketFactory;
import com.aleiye.lassock.live.basket.DefaultBasketFactory;
import com.aleiye.lassock.live.basket.MemoryQueueBasket;
import com.aleiye.lassock.live.bazaar.Bazaar;
import com.aleiye.lassock.live.bazaar.BazaarFactory;
import com.aleiye.lassock.live.bazaar.BazaarRunner;
import com.aleiye.lassock.live.bazaar.DefaultBazaarFactory;
import com.aleiye.lassock.live.hill.Hill;
import com.aleiye.lassock.logging.Logging;
import com.aleiye.lassock.monitor.DefultMonitor;
import com.aleiye.lassock.monitor.Monitor;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.DestroyableUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

/**
 * 内容容器
 *
 * @author ruibing.zhao
 * @since 2015年5月19日
 * @version 2.2.1
 */
public class LiveContainer extends Logging implements Destroyable {
	/** 队列*/
	private Map<String, Basket> baskets;
	/**采集管理*/
	private Hill hill;
	/** 监控器*/
	private Monitor monitor;

	/** 集市*/
	private Map<String, BazaarRunner> bazaarRunners;

	public LiveContainer(Config config) throws Exception {
		// this.config = config;
	}

	/**
	 * 初始组件
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		loadBaskets();

		// hill = (Hill) new DefaultHill();
		// hill.setBaskets(baskets);
		// hill.initialize();
		hill = new HillMirror();
		hill.setBaskets(baskets);
		hill.initialize();
		// 采集图管理初始化
		// hillsMap = new HillsMap(basketMap);

		loadBazaars();

		Monitor monitor = new DefultMonitor(hill);
		monitor.configure(ConfigUtils.getContext("monitor"));
		monitor.setName("lassock-monitor");
		monitor.start();
	}

	private void loadBaskets() throws Exception {
		baskets = new HashMap<String, Basket>();
		Basket defualt = new MemoryQueueBasket();
		defualt.setName("_DEFUALT");
		baskets.put(defualt.getName(), defualt);
		BasketFactory factory = new DefaultBasketFactory();
		// 加载自定义扩展采集源
		Config config = ConfigUtils.getConfig().getConfig("live.baskets");
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String key = "basket" + i;
			if (!containsKey(config, key)) {
				break;
			}
			Config conf = config.getConfig(key);
			String name = conf.getString("name");
			String strClass = conf.getString("class");
			Basket basket = factory.create(name, strClass);
			basket.start();
			baskets.put(name, basket);
		}
	}

	private void loadBazaars() throws Exception {
		bazaarRunners = new HashMap<String, BazaarRunner>();
		BazaarFactory factory = new DefaultBazaarFactory();
		// 加载自定义扩展采集源
		Config config = ConfigUtils.getConfig().getConfig("live.bazaars");
		Basket defaultBasket = baskets.get("_DEFUALT");
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String key = "bazaar" + i;
			if (!containsKey(config, key)) {
				break;
			}
			Config conf = config.getConfig(key);
			String name = conf.getString("name");
			String strClass = conf.getString("class");
			Bazaar bazaar = factory.create(name, strClass);
			if (containsKey(conf, "basket")) {
				String bk = conf.getString("basket");
				if (!baskets.containsKey(bk)) {
					throw new Exception("Cant find basket wiht name:" + bk);
				}
				Basket basket = baskets.get(bk);
				bazaar.setBasket(basket);
			} else {
				bazaar.setBasket(defaultBasket);
			}
			bazaar.configure(ConfigUtils.toContext(conf));
			BazaarRunner runner = BazaarRunner.forBazaar(bazaar);
			runner.start();
			bazaarRunners.put(name, runner);
		}
	}

	private boolean containsKey(Config conf, String key) {
		try {
			conf.getObject(key);
		} catch (ConfigException.WrongType e) {
			return true;
		} catch (Exception e1) {
			return false;
		}
		return true;
	}

	public Live live() {
		return hill;
	}

	@Override
	public void destroy() {
		// 关闭监控
		monitor.stop();
		// 采集关闭
		DestroyableUtils.destroyQuietly(hill);
		// 集市关闭
		for (BazaarRunner br : bazaarRunners.values()) {
			br.stop();
		}
		bazaarRunners.clear();
		// 对列关闭
		for (Basket bk : baskets.values()) {
			bk.stop();
		}
		baskets.clear();

	}
}
