package com.aleiye.lassock.live.hill.shade;

import com.aleiye.lassock.live.hill.PollableShade;
import com.aleiye.lassock.util.LogUtils;

public abstract class AbstractPollableShade extends BasicShadeSemantics implements PollableShade {

	public AbstractPollableShade() {
		super();
	}

	@Override
	public void pick() throws Exception {
		Exception exception = getStartException();
		if (exception != null) {
			throw new Exception("Shade had error configuring or starting", exception);
		}
		assertStarted();
		try {
			doPick();
		} catch (Exception e) {
			this.intelligence.setErrorCount(this.intelligence.getErrorCount() + 1);
			LogUtils.error(this.getName() + " pick Exception!\n" + e.getMessage());
			throw e;
		}

	}

	protected abstract void doPick() throws Exception;
}
