package com.sillim.recordit.member.dto.response;

public record KakaoOidcPublicKey(String kid,
                                 String alg,
                                 String kty,
                                 String use,
                                 String n,
                                 String e) {

}
