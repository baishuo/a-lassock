package com.aleiye.lassock.live.hill.shade;


public interface ShadeFactory {

	public Shade create(String sourceName, String type) throws Exception;

	public Class<? extends Shade> getClass(String type) throws Exception;
}
