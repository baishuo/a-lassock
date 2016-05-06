package com.aleiye.lassock.live.hill.source;


public interface ShadeFactory {

	public Source create(String sourceName, String type) throws Exception;

	public Class<? extends Source> getClass(String type) throws Exception;
}
