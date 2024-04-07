package com.sillim.recordit.member.dto.kakao;

public record KakaoOidcPublicKey(String kid,
								 String alg,
								 String kty,
								 String use,
								 String n,
								 String e) {}
