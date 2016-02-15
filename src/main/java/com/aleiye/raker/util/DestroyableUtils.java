package com.aleiye.raker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.raker.common.able.Destroyable;
import com.aleiye.raker.common.able.Destroyables;

/**
 * 
 * 
 * @author ruibing.zhao
 * @since 2015年5月28日
 * @version 2.2.1
 */
public class DestroyableUtils {
	private static final Logger log = LoggerFactory.getLogger(DestroyableUtils.class);

	public static void destroyQuietly(Destroyable destroyable) {
		try {
			Destroyables.destroy(destroyable, true);
		} catch (Exception e) {
			log.error("Exception should not have been thrown.", e);
		}
	}
}
