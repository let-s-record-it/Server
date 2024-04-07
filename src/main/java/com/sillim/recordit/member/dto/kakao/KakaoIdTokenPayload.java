package com.sillim.recordit.member.dto.kakao;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record KakaoIdTokenPayload(String iss, String aud, Long exp, String sub) {

	private static final String ISS = "https://kauth.kakao.com";

	public String getUserId() {
		return sub;
	}

	public void validatePayload(String aud) {
		if (!equalsIss()) {
			throw new InvalidIdTokenException(ErrorCode.ID_TOKEN_UNSUPPORTED);
		}

		if (!equalsAud(aud)) {
			throw new InvalidIdTokenException(ErrorCode.ID_TOKEN_INVALID_KEY);
		}

		if (isValidTime(LocalDateTime.now())) {
			throw new InvalidIdTokenException(ErrorCode.ID_TOKEN_EXPIRED);
		}
	}

	private boolean equalsIss() {
		return this.iss.equals(ISS);
	}

	private boolean equalsAud(String appKey) {
		return this.aud.equals(appKey);
	}

	private boolean isValidTime(LocalDateTime standTime) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault())
				.isBefore(standTime);
	}
}
