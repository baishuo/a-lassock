package com.aleiye.lassock.liveness;

import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.live.conf.Context;

/**
 * Liveness 配置类
 * 
 * @author ruibing.zhao
 * @since 2016年2月25日
 * @version 1.0
 */
public class LivenessConfiguration {
	private final Context context;
	private final Live live;

	public LivenessConfiguration(Context context, Live live) {
		this.context = context;
		this.live = live;
	}

	/**
	 * 获取Liveness 实例
	 * @return
	 * @throws Exception
	 */
	public Liveness getInstance() throws Exception {
		Class<?> livenessClass = Class.forName(context.getString("class"));
		Liveness liveness = (Liveness) livenessClass.newInstance();
		// 挂钩Live
		liveness.setLive(live);
		liveness.configure(context);
		return liveness;
	}
}
