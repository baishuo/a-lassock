package com.aleiye.lassock.liveness;

import com.aleiye.lassock.live.conf.Context;

public class LivenessConfiguration {
	private final Context context;

	public LivenessConfiguration(Context context) {
		this.context = context;
	}

	public Liveness getInstance() throws Exception {
		Class<?> livenessClass = Class.forName(context.getString("class"));
		Liveness liveness = (Liveness) livenessClass.newInstance();
		liveness.configure(context);
		return liveness;
	}
}
