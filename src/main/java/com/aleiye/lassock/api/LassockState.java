package com.aleiye.lassock.api;

import java.io.Serializable;

/**
 * 
 * 
 * @author ruibing.zhao
 * @since 2016年2月23日
 * @version 1.0
 */
public class LassockState implements Serializable {
	private static final long serialVersionUID = 1L;
	// 采集器当前状态
	private RunState state;
	private long scrollCount;

	public RunState getState() {
		return state;
	}

	public void setState(RunState state) {
		this.state = state;
	}

	public long getScrollCount() {
		return scrollCount;
	}

	public void setScrollCount(long scrollCount) {
		this.scrollCount = scrollCount;
	}

	public static enum RunState {
		RUNNING, SHUTDOWN, PASED
	}

}
