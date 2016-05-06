package com.aleiye.lassock.live.model;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aleiye.lassock.util.JsonProvider;

public class MushroomBuilder {

	public static Mushroom withBody(byte[] body, Map<String, String> headers) {
		Mushroom event = new GeneralMushroom();

		if (body == null) {
			body = new byte[0];
		}
		event.setBody(body);

		if (headers != null) {
			event.setHeaders(new HashMap<String, String>(headers));
		}

		return event;
	}

	// byte
	public static Mushroom withBody(byte[] body) {
		return withBody(body, null);
	}

	// string
	public static Mushroom withBody(String body, Charset charset, Map<String, String> headers) {

		return withBody(body.getBytes(charset), headers);
	}

	public static Mushroom withBody(String body, Charset charset) {
		return withBody(body, charset, null);
	}

	public static Mushroom withBody(Map<?, ?> body, Map<String, String> headers) {
		try {
			return withBody(JsonProvider.mapper.writeValueAsBytes(body), headers);
		} catch (Exception e) {
			//
		}
		return null;
	}

	public static Mushroom withBody(List<?> body, Map<String, String> headers) {
		try {
			return withBody(JsonProvider.mapper.writeValueAsBytes(body), headers);
		} catch (Exception e) {
			//
		}
		return null;
	}
}
