package com.sillim.recordit.config.security.oauth2;

import com.sillim.recordit.member.domain.OAuthProvider;

public record OAuth2Request(String account, OAuthProvider provider, String name, String imageUrl, String email) {
}
