package com.sillim.recordit.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// global
	INVALID_ARGUMENT("ERR_GLOBAL_001", "올바르지 않은 값이 전달되었습니다."),
	REQUEST_NOT_FOUND("ERR_GLOBAL_002", "요청을 찾을 수 없습니다."),
	UNHANDLED_EXCEPTION("ERR_GLOBAL_999", "예상치 못한 오류가 발생했습니다."),

	// monthly-goal
	MONTHLY_GOAL_NOT_FOUND("ERR_MONTHLY_GOAL_001", "존재하지 않는 월 목표입니다.");

	private final String code;
	private final String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
