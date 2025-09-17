package com.sillim.recordit.config.filter;

import lombok.Getter;

@Getter
public class LimitApi {

	private final String method;
	private final String url;

	private LimitApi(String method, String url) {
		this.method = method;
		this.url = url;
	}

	public static LimitApi pattern(String method, String url) {
		return new LimitApi(method, url);
	}

	public static LimitApi pattern(String url) {
		return new LimitApi(null, url);
	}

	public boolean noMethod() {
		return this.method == null;
	}
}
