package com.sillim.recordit.member.dto.oidc;

public record OidcPublicKey(String kid, String alg, String kty, String use, String n, String e) {
}
