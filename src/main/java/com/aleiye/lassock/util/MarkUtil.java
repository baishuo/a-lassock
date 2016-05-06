package com.aleiye.lassock.util;

import com.aleiye.lassock.live.mark.Marker;
import com.aleiye.lassock.live.mark.SimpleMarker;

/**
 * 标记共享工具
 * 
 * @author ruibing.zhao
 * @since 2015年5月28日
 * @version 2.2.1
 */
public class MarkUtil {
	// 初始
	public static Marker<Long> marker = new SimpleMarker<Long>();

	public static void mark(String key, long mark) throws InterruptedException {
		marker.mark(key, mark);
	}

	public static void reMark(String key) {
		marker.reMark(key);
	}

	public static void setMarker(Marker<Long> marker1) {
		marker = marker1;
	}

	public static Long getMark(String key) {
		Long r = marker.getMark(key);
		if (r == null) {
			return 0L;
		}
		return r;
	}
}
