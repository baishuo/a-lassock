package com.aleiye.lassock.live.interceptor;

import java.util.List;

import com.aleiye.lassock.common.able.Configurable;
import com.aleiye.lassock.model.Mushroom;

public interface Interceptor {
	public void initialize();

	public Mushroom intercept(Mushroom event);

	public List<Mushroom> intercept(List<Mushroom> events);

	public void close();

	public interface Builder extends Configurable {
		public Interceptor build();
	}
}
