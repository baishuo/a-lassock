package com.aleiye.lassock.live.hill;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.conf.shade.ShadeType;
import com.google.common.base.Preconditions;

public class DefaultShadeFactory implements ShadeFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultShadeFactory.class);

	@Override
	public Shade create(String name, String type) throws Exception {
		Preconditions.checkNotNull(name, "name");
		Preconditions.checkNotNull(type, "type");
		LOGGER.info("Creating instance of shade {}, type {}", name, type);
		Class<? extends Shade> shadeClass = getClass(type);
		try {
			Shade source = shadeClass.newInstance();
			source.setName(name);
			return source;
		} catch (Exception ex) {
			throw new Exception("Unable to create source: " + name + ", type: " + type + ", class: "
					+ shadeClass.getName(), ex);
		}
	}

	@Override
	public Class<? extends Shade> getClass(String type) throws Exception {
		Class<? extends Shade> sourceClass = null;
		ShadeType srcType = ShadeType.OTHER;
		try {
			srcType = ShadeType.valueOf(type.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException ex) {
			LOGGER.debug("Shade type {} is a custom type", type);
		}
		if (!srcType.equals(ShadeType.OTHER)) {
			sourceClass = srcType.getShadeClass();
		}
		return sourceClass;
	}

}
