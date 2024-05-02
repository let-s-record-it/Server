package com.sillim.recordit.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// global
	INVALID_ARGUMENT("ERR_GLOBAL_001", "올바르지 않은 값이 전달되었습니다."),
	REQUEST_NOT_FOUND("ERR_GLOBAL_002", "요청을 찾을 수 없습니다."),
	UNHANDLED_EXCEPTION("ERR_GLOBAL_999", "예상치 못한 오류가 발생했습니다."),

	ID_TOKEN_UNSUPPORTED("ERR_OIDC_001", "지원되지 않는 ID Token 입니다."),
	ID_TOKEN_EXPIRED("ERR_OIDC_002", "ID Token이 만료되었습니다."),
	ID_TOKEN_INVALID_KEY("ERR_OIDC_003", "App Key가 유효하지 않습니다."),
	ID_TOKEN_INVALID_SIGNATURE("ERR_OIDC_004", "ID Token의 Signature가 유효하지 않습니다."),

	JWT_MALFORMED("ERR_JWT_001", "JWT가 손상되었습니다."),
	JWT_EXPIRED("ERR_JWT_002", "JWT가 만료되었습니다."),
	JWT_UNSUPPORTED("ERR_JWT_003", "지원되지 않는 JWT 형식입니다."),
	JWT_INVALID_SIGNATURE("ERR_JWT_004", "JWT의 Signature가 유효하지 않습니다."),

	MEMBER_NOT_FOUND("MEMBER_001", "요청한 멤버를 찾을 수 없습니다."),

	INVALID_KAKAO_TOKEN("ERR_KAKAO_001", "유효하지 않은 Kakao Access Token입니다."),
	// monthly-goal
	MONTHLY_GOAL_NOT_FOUND("ERR_MONTHLY_GOAL_001", "존재하지 않는 월 목표입니다.");

	private final String code;
	private final String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
