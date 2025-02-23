package com.sillim.recordit.member.dto.request;

import com.sillim.recordit.member.domain.OAuthProvider;

public record LoginRequest(
		String idToken,
		String accessToken,
		OAuthProvider provider,
		String deviceId,
		String model,
		String fcmToken) {}
