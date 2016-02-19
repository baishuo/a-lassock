package com.aleiye.lassock.live.bazaar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.conf.Context;
import com.aleiye.lassock.model.Mushroom;

public class LoggerBazaar extends AbstractBazaar {
	private static final Logger logger = LoggerFactory.getLogger(LoggerBazaar.class);

	@Override
	public void process() throws Exception {
		Basket channel = getBasket();
		Mushroom event = null;

		try {
			event = channel.take();

			if (event != null) {
				if (logger.isInfoEnabled()) {
					logger.info("Event: " + new String((byte[]) event.getBody()));
				}
			} else {
				// No event found, request back-off semantics from the sink
				// runner
			}
		} catch (Exception ex) {}
	}

	@Override
	public void configure(Context context) {
		//
	}
}
