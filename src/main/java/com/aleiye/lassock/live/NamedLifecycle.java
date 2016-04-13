package com.aleiye.lassock.live;

import com.aleiye.lassock.common.NamedComponent;
import com.aleiye.lassock.lifecycle.AbstractLifecycleAware;

/**
 * 名称和生命周期抽像
 * 
 * @author ruibing.zhao
 * @since 2016年2月18日
 * @version 1.0
 */
public abstract class NamedLifecycle extends AbstractLifecycleAware implements NamedComponent {
	// 名称
	private String name;

	@Override
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "{name:" + name + ",state:" + lifecycleState + "}";
	}
}
