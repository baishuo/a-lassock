package com.aleiye.lassock.common.able;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Destroyables {

	static final Logger logger = Logger.getLogger(Destroyables.class.getName());

	private Destroyables() {}

	public static void destroy(Destroyable destroyable, boolean swallowException) throws Exception {
		if (destroyable == null) {
			return;
		}
		try {
			destroyable.destroy();
		} catch (Exception e) {
			if (swallowException) {
				logger.log(Level.WARNING, "Exception thrown while destroying Destroyable.", e);
			} else {
				throw e;
			}
		}
	}

}
