package com.sillim.recordit.member.dto.response;

public record OAuthTokenResponse(String accessToken, String refreshToken, Boolean activated) {}
