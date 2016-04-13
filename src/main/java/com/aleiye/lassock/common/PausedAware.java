package com.aleiye.lassock.common;

/**
 * 暂停
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public interface PausedAware {
	/**
	 * 恢复
	 */
	void resume();

	/**
	 * 暂停
	 */
	void pause();

	/**
	 * 是否暂停
	 * @return 
	 */
	boolean isPaused();
}
