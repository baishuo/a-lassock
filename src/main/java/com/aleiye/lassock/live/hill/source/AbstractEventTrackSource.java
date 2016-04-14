package com.aleiye.lassock.live.hill.source;

import com.aleiye.lassock.util.LogUtils;

public abstract class AbstractEventTrackSource extends BasicSourceSemantics implements EventTrackSource {

	@Override
	public void pick() throws Exception {
		Exception exception = this.sign.getException();
		if (exception != null) {
			throw new Exception("Shade had error configuring or starting", exception);
		}
		assertStarted();
		try {
			doPick();
		} catch (Exception e) {
			this.sign.getIntelligence().setErrorCount(this.sign.getIntelligence().getErrorCount() + 1);
			LogUtils.error(this.getName() + " pick Exception!\n" + e.getMessage());
			throw e;
		}
	}

	protected abstract void doPick() throws Exception;
}
