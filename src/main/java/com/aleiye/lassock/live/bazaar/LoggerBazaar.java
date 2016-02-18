package com.aleiye.lassock.live.bazaar;

import java.util.List;
import java.util.Map;

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
					if (event.getBody() instanceof byte[]) {
						logger.info("Event: " + new String((byte[]) event.getBody()));
					} else if (event.getBody() instanceof Map) {
						logger.info("Event: " + event.getBody().toString());
					} else if (event.getBody() instanceof List) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) event.getBody();
						for (Map<String, Object> map : list) {
							logger.info("Event: " + map.toString());
						}
					}
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
