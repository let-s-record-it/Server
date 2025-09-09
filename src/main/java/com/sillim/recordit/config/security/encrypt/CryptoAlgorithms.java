package com.sillim.recordit.config.security.encrypt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CryptoAlgorithms {
	SHA_256("SHA-256"), AES("AES"), AES_ECB_PKCS5PADDING("AES/ECB/PKCS5Padding"), AES_CBC_PKCS5PADDING(
			"AES/CBC/PKCS5Padding"),;

	private String value;
}
