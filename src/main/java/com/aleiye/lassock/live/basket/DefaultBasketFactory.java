package com.aleiye.lassock.live.basket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class DefaultBasketFactory implements BasketFactory {
	private static final Logger logger = LoggerFactory.getLogger(DefaultBasketFactory.class);

	@Override
	public Basket create(String name, String clazz) throws Exception {
		Preconditions.checkNotNull(name, "name");
		Preconditions.checkNotNull(clazz, "type");
		logger.info("Creating instance of basket {} type {}", name, clazz);
		Class<? extends Basket> basketClass = getClass(clazz);
		try {
			Basket basket = basketClass.newInstance();
			basket.setName(name);
			return basket;
		} catch (Exception ex) {
			throw new Exception("Unable to create basket: " + name + ", class: " + clazz + "", ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Basket> getClass(String type) throws Exception {
		// String channelClassName = type;
		// ChannelType channelType = ChannelType.OTHER;
		// try {
		// channelType = ChannelType.valueOf(type.toUpperCase(Locale.ENGLISH));
		// } catch (IllegalArgumentException ex) {
		// logger.debug("Channel type {} is a custom type", type);
		// }
		// if (!channelType.equals(ChannelType.OTHER)) {
		// channelClassName = channelType.getChannelClassName();
		// }
		// try {
		return (Class<? extends Basket>) Class.forName(type);
		// } catch (Exception ex) {
		// throw new FlumeException("Unable to load channel type: " + type +
		// ", class: " + channelClassName, ex);
		// }
	}

}
