package com.aleiye.raker.live.basket;

import com.aleiye.raker.model.Mushroom;

public class SimpleLogoutBasket extends AbstractBasket {

	@Override
	public void push(Mushroom mushroom) {
		System.err.println(mushroom.getSignId());
		if (mushroom.getContent().getClass().isAssignableFrom(byte[].class)) {
			System.err.println(new String((byte[]) mushroom.getContent()));
		} else {
			System.err.println(mushroom.getContent().toString());
		}
	}

	@Override
	public Mushroom take() {
		return null;
	}
}
