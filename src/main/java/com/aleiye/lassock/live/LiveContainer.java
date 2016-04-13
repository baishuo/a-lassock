package com.aleiye.lassock.live;

import com.aleiye.lassock.api.conf.Context;
import com.aleiye.lassock.common.InitializeAware;
import com.aleiye.lassock.conf.ConfigurationConstants;
import com.aleiye.lassock.conf.LiveConfiguration;
import com.aleiye.lassock.live.hill.Hill;
import com.aleiye.lassock.live.station.BasketStation;
import com.aleiye.lassock.live.station.BazaarStation;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.DestroyableUtils;
import com.typesafe.config.Config;

/**
 * 采集生涯容器
 *
 * @author ruibing.zhao
 * @since 2015年5月19日
 * @version 2.2.1
 */
public class LiveContainer implements InitializeAware {
	// 配置信息
	private Config config;
	// 队列
	private BasketStation basketStation;
	// 消费端
	private BazaarStation bazaarStation;
	// 采集管理
	private Hill hill;

	public LiveContainer(Config config) throws Exception {
		this.config = config;
	}

	@Override
	public void initialize() throws Exception {
		Context liveContext = ConfigUtils.toContext(config.getConfig(ConfigurationConstants.CONFIG_LIVE));
		LiveConfiguration configuration = new LiveConfiguration(liveContext);
		// 加载队列
		basketStation = new BasketStation(configuration);
		basketStation.initialize();
		// 加载消费端
		bazaarStation = new BazaarStation(configuration, basketStation);
		bazaarStation.initialize();
		// 加载采集源
		hill = new HillMirror();
		hill.setBaskets(basketStation);
		hill.initialize();
	}

	public Live live() {
		return hill;
	}

	@Override
	public void destroy() {
		// 采集关闭
		DestroyableUtils.destroyQuietly(hill);
		// 消费关闭
		bazaarStation.destroy();
		// 对列关闭
		basketStation.destroy();
	}
}
