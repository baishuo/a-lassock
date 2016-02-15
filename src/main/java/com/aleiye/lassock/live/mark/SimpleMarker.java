package com.aleiye.lassock.live.mark;

import java.io.IOException;

/**
 * 一个空的Marker(用于Marker不开启时 应用调用Marker时的空指针)
 * 
 * @author ruibing.zhao
 * @since 2015年5月28日
 * @version 2.1.2
 */
public class SimpleMarker<T> implements Marker<T> {

	@Override
	public void load() {}

	@Override
	public void mark(String key, T t) {}

	@Override
	public void save() throws Exception {}

	@Override
	public T getMark(String key) {
		return null;
	}

	@Override
	public void close() throws IOException {}
}
