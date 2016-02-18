package com.aleiye.lassock.lifecycle;

/**
 * 生命周期
 * 
 * @author ruibing.zhao
 * @since 2015年11月17日
 */
public interface LifecycleAware {

	public void start();

	public void stop();

	public LifecycleState getLifecycleState();
}
