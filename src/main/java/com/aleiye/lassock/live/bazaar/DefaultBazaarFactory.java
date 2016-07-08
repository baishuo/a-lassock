package com.aleiye.lassock.live.bazaar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class DefaultBazaarFactory implements BazaarFactory {
	private static final Logger logger = LoggerFactory.getLogger(DefaultBazaarFactory.class);

	@Override
	public Bazaar create(String name, String clazz) throws Exception {
		Preconditions.checkNotNull(name, "name");
		Preconditions.checkNotNull(clazz, "type");
		logger.info("Creating instance of bazaar {} type {}", name, clazz);
		Class<? extends Bazaar> bazaarClass = getClass(clazz);
		try {
			Bazaar bazaar = bazaarClass.newInstance();
			bazaar.setName(name);
			return bazaar;
		} catch (Exception ex) {
			throw new Exception("Unable to create bazaar: " + name + ", class: " + clazz + "", ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Bazaar> getClass(String type) throws Exception {
		return (Class<? extends Bazaar>) Class.forName(type);
	}

}
