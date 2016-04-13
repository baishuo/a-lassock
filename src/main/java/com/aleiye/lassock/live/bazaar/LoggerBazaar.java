package com.aleiye.lassock.live.bazaar;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.model.Mushroom;

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
					logger.info(JSONObject.fromObject(event.getHeaders()).toString());
					event.incrementCompleteCount();
				}
			}
		} catch (Exception ex) {}
	}

	@Override
	public void configure(Context context) {
		//
	}
}
