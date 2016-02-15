package com.aleiye.lassock.live.bazaar;

public interface BazaarFactory {
	public Bazaar create(String name, String clazz) throws Exception;

	public Class<? extends Bazaar> getClass(String type) throws Exception;
}
