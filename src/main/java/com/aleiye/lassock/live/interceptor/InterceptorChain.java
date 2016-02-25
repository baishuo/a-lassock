package com.aleiye.lassock.live.interceptor;

import com.aleiye.lassock.live.model.Mushroom;
import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class InterceptorChain implements Interceptor {

	// list of interceptors that will be traversed, in order
	private List<Interceptor> interceptors;

	public InterceptorChain() {
		interceptors = Lists.newLinkedList();
	}

	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public Mushroom intercept(Mushroom event) {
		for (Interceptor interceptor : interceptors) {
			if (event == null) {
				return null;
			}
			event = interceptor.intercept(event);
		}
		return event;
	}

	@Override
	public List<Mushroom> intercept(List<Mushroom> events) {
		for (Interceptor interceptor : interceptors) {
			if (events.isEmpty()) {
				return events;
			}
			events = interceptor.intercept(events);
			Preconditions.checkNotNull(events, "Mushroom list returned null from interceptor %s", interceptor);
		}
		return events;
	}

	@Override
	public void initialize() {
		Iterator<Interceptor> iter = interceptors.iterator();
		while (iter.hasNext()) {
			Interceptor interceptor = iter.next();
			interceptor.initialize();
		}
	}

	@Override
	public void close() {
		Iterator<Interceptor> iter = interceptors.iterator();
		while (iter.hasNext()) {
			Interceptor interceptor = iter.next();
			interceptor.close();
		}
	}

}
