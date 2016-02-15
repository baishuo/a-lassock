package com.aleiye.raker.live.mark;

import java.io.Closeable;

/**
 * 采集标记
 * 
 * @author ruibing.zhao
 * @since 2015年5月28日
 * @version 2.2.1
 */
public interface Marker<T> extends Closeable {
	// 加载标记资源
	public void load() throws Exception;

	// 保存标记次源
	public void save() throws Exception;

	// 打标
	public void mark(String key, T t);

	public T getMark(String key);
}
