package com.aleiye.lassock.live.hill.shade.tool;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.util.DestroyableUtils;
import com.aleiye.lassock1.live.hills.Hill1;
import com.aleiye.lassock1.live.hills.text.TextHill;

public class ShadeFileExecutor {

	// 周期性的任务执行的线程池
	private static Hill1 hill1;

	private ShadeFileExecutor() {}

	private static LifecycleState lifecycleState;

	public static void start() {
		hill1 = new TextHill();
		try {
			hill1.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		lifecycleState = LifecycleState.START;
	}

	public static void shutdown() {
		DestroyableUtils.destroyQuietly(hill1);
		lifecycleState = LifecycleState.STOP;
	}

	public static boolean isStarted() {
		return lifecycleState == LifecycleState.START;
	}

	public static Hill1 getService() {
		return hill1;
	}

}
