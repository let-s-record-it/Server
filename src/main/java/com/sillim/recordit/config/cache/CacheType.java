package com.sillim.recordit.config.cache;

import lombok.Getter;

@Getter
public enum CacheType {
	PUBLIC_KEYS("publicKeys", 60 * 60, 20),
	;

	private final String cacheName;
	private final int expireAfterWrite;
	private final int maximumSize;

	CacheType(String cacheName, int expireAfterWrite, int maximumSize) {
		this.cacheName = cacheName;
		this.expireAfterWrite = expireAfterWrite;
		this.maximumSize = maximumSize;
	}
}
