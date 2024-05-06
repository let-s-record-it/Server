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

	MEMBER_NOT_FOUND("ERR_MEMBER_001", "요청한 멤버를 찾을 수 없습니다."),

	INVALID_KAKAO_TOKEN("ERR_KAKAO_001", "유효하지 않은 Kakao Access Token입니다."),

	NULL_SCHEDULE_TITLE("ERR_SCHEDULE_001", "일정 제목은 null일 수 없습니다."),
	BLANK_SCHEDULE_TITLE("ERR_SCHEDULE_002", "일정 제목은 빈 값일 수 없습니다."),
	INVALID_SCHEDULE_TITLE_LENGTH("ERR_SCHEDULE_003", "일정 제목의 길이는 30자를 넘을 수 없습니다."),
	NULL_SCHEDULE_DESCRIPTION("ERR_SCHEDULE_004", "일정 설명은 null일 수 없습니다."),
	INVALID_SCHEDULE_DESCRIPTION_LENGTH("ERR_SCHEDULE_005", "설명의 길이는 500자를 넘을 수 없습니다."),
	NULL_SCHEDULE_COLOR_HEX("ERR_SCHEDULE_006", "일정 색상 값은 null일 수 없습니다."),
	INVALID_SCHEDULE_COLOR_HEX("ERR_SCHEDULE_007", "유효하지 않은 일정 색상 값입니다."),
	LATITUDE_OUT_OF_RANGE("ERR_SCHEDULE_008", "위도 값은 0 이상 90 이하여야 합니다."),
	LONGITUDE_OUT_OF_RANGE("ERR_SCHEDULE_009", "경도 값은 0 이상 180 이하여야 합니다."),
	INVALID_DURATION("ERR_SCHEDULE_010", "일정 시작 시간은 종료 시간보다 클 수 없습니다."),
	INVALID_REPETITION_PERIOD("ERR_SCHEDULE_011", "일정 반복 주기는 1 이상 999 이하여야 합니다."),
	INVALID_MONTH_OF_YEAR("ERR_SCHEDULE_012", "월은 1 이상 12 이하여야 입니다."),
	INVALID_DAY_OF_MONTH("ERR_SCHEDULE_013", "유효하지 않은 일입니다."),
	INVALID_REPETITION_TYPE("ERR_SCHEDULE_014", "유효하지 않은 반복 타입입니다."),
	WEEKDAY_BIT_OUT_OF_RANGE("ERR_SCHEDULE_015", "요일 비트는 1 이상 127 이하여야 합니다."),
	NOT_EQUAL_DAY_OF_MONTH("ERR_SCHEDULE_016", "dayOfMonth와 startDate의 월이 일치하지 않습니다."),
	NOT_EQUAL_MONTH_OF_YEAR("ERR_SCHEDULE_017", "monthOfYear과 startDate의 년이 일치하지 않습니다."),
	SCHEDULE_NOT_FOUND("ERR_SCHEDULE_018", "요청한 일정을 찾을 수 없습니다."),
	SCHEDULE_GROUP_NOT_FOUND("ERR_SCHEDULE_019", "요청한 일정 그룹을 찾을 수 없습니다."),
	REPETITION_PATTERN_NOT_FOUND("ERR_SCHEDULE_020", "요청한 반복 패턴을 찾을 수 없습니다."),

	NULL_CALENDAR_TITLE("ERR_CALENDAR_001", "캘린더 제목은 null일 수 없습니다."),
	BLANK_CALENDAR_TITLE("ERR_CALENDAR_002", "캘린더 제목은 빈 값일 수 없습니다."),
	INVALID_CALENDAR_TITLE_LENGTH("ERR_CALENDAR_003", "캘린더 제목의 길이는 30자를 넘을 수 없습니다."),
	INVALID_CALENDAR_COLOR_HEX("ERR_CALENDAR_004", "유효하지 않은 캘린더 색상 값입니다."),
	NULL_CALENDAR_COLOR_HEX("ERR_CALENDAR_005", "캘린더 색상 값은 null일 수 없습니다."),
	CALENDAR_NOT_FOUND("ERR_CALENDAR_006", "요청한 캘린더를 찾을 수 없습니다."),

	// monthly-goal
	MONTHLY_GOAL_NOT_FOUND("ERR_MONTHLY_GOAL_001", "존재하지 않는 월 목표입니다.");

	private final String code;
	private final String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
