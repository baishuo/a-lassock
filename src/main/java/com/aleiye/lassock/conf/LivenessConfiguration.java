package com.aleiye.lassock.conf;

import com.aleiye.lassock.api.conf.Context;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.liveness.Liveness;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

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
		final Liveness liveness = (Liveness) livenessClass.newInstance();
		EventBus eventBus = new EventBus(new SubscriberExceptionHandler() {
			@Override
			public void handleException(Throwable exception, SubscriberExceptionContext context) {
				liveness.ExceptionHandler(exception, context);
			}
		});
		eventBus.register(this.live);
		// 挂钩Live
		liveness.setEventBus(eventBus);
		liveness.configure(context);
		return liveness;
	}
}
