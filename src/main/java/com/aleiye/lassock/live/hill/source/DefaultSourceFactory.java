package com.aleiye.lassock.live.hill.source;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.conf.shade.SourceType;
import com.google.common.base.Preconditions;

public class DefaultSourceFactory implements ShadeFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSourceFactory.class);

	@Override
	public Source create(String name, String type) throws Exception {
		Preconditions.checkNotNull(name, "name");
		Preconditions.checkNotNull(type, "type");
		LOGGER.info("Creating instance of shade {}, type {}", name, type);
		Class<? extends Source> shadeClass = getClass(type);
		try {
			Source source = shadeClass.newInstance();
			source.setName(name);
			return source;
		} catch (Exception ex) {
			throw new Exception("Unable to create source: " + name + ", type: " + type + ", class: "
					+ shadeClass.getName(), ex);
		}
	}

	@Override
	public Class<? extends Source> getClass(String type) throws Exception {
		Class<? extends Source> sourceClass = null;
		SourceType srcType = SourceType.OTHER;
		try {
			srcType = SourceType.valueOf(type.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException ex) {
			LOGGER.debug("Shade type {} is a custom type", type);
		}
		if (!srcType.equals(SourceType.OTHER)) {
			sourceClass = srcType.getShadeClass();
		}
		return sourceClass;
	}

}
