package com.aleiye.lassock.util;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;

/**
 * Closeable实例类关闭辅助
 */
public class CloseableUtils {
	private static final Logger log = LoggerFactory.getLogger(CloseableUtils.class);

	public static void closeQuietly(Closeable closeable) {
		try {
			// Here we've instructed Guava to swallow the IOException
			Closeables.close(closeable, true);
		} catch (IOException e) {
			// We instructed Guava to swallow the IOException, so this should
			// never happen. Since it did, log it.
			log.error("IOException should not have been thrown.", e);
		}
	}
}
