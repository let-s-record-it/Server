package com.sillim.recordit.config.security.jwt;

public record AuthorizationToken(String accessToken, String refreshToken) {}
