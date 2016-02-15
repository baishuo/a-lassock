package com.aleiye.raker.live.basket;

public interface BasketFactory {
	public Basket create(String name, String clazz) throws Exception;

	public Class<? extends Basket> getClass(String type) throws Exception;
}
