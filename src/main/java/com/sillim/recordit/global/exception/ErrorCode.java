package com.sillim.recordit.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// global
	INVALID_ARGUMENT("ERR_GLOBAL_001", "올바르지 않은 값이 전달되었습니다."),
	REQUEST_NOT_FOUND("ERR_GLOBAL_002", "요청을 찾을 수 없습니다."),
	INVALID_REQUEST("ERR_GLOBAL_003", "유효하지 않은 요청입니다."),
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
	NOT_EQUAL_DAY_OF_MONTH("ERR_SCHEDULE_016", "dayOfMonth와 startDate의 일이 일치하지 않습니다."),
	NOT_EQUAL_MONTH_OF_YEAR("ERR_SCHEDULE_017", "monthOfYear와 startDate의 월이 일치하지 않습니다."),
	SCHEDULE_NOT_FOUND("ERR_SCHEDULE_018", "요청한 일정을 찾을 수 없습니다."),
	SCHEDULE_GROUP_NOT_FOUND("ERR_SCHEDULE_019", "요청한 일정 그룹을 찾을 수 없습니다."),
	REPETITION_PATTERN_NOT_FOUND("ERR_SCHEDULE_020", "요청한 반복 패턴을 찾을 수 없습니다."),

	NULL_CALENDAR_TITLE("ERR_CALENDAR_001", "캘린더 제목은 null일 수 없습니다."),
	BLANK_CALENDAR_TITLE("ERR_CALENDAR_002", "캘린더 제목은 빈 값일 수 없습니다."),
	INVALID_CALENDAR_TITLE_LENGTH("ERR_CALENDAR_003", "캘린더 제목의 길이는 30자를 넘을 수 없습니다."),
	INVALID_CALENDAR_COLOR_HEX("ERR_CALENDAR_004", "유효하지 않은 캘린더 색상 값입니다."),
	NULL_CALENDAR_COLOR_HEX("ERR_CALENDAR_005", "캘린더 색상 값은 null일 수 없습니다."),
	CALENDAR_NOT_FOUND("ERR_CALENDAR_006", "요청한 캘린더를 찾을 수 없습니다."),
	INVALID_CALENDAR_DELETE_REQUEST("ERR_CALENDAR_007", "유효하지 않은 캘린더 삭제 요청입니다."),
	CALENDAR_ACCESS_DENIED("ERR_CALENDAR_008", "해당 사용자는 접근할 수 없는 캘린더입니다."),

	// monthly-goal
	MONTHLY_GOAL_NOT_FOUND("ERR_MONTHLY_GOAL_001", "존재하지 않는 월 목표입니다."),
	DIFFERENT_YEAR_MONTH("ERR_MONTHLY_GOAL_002", "월 목표 기간 시작일과 종료일의 년,월은 같아야 합니다."),
	INVALID_START_DAY_OF_MONTH("ERR_MONTHLY_GOAL_003", "월 목표 기간의 시작일은 1일이어야 합니다."),
	INVALID_END_DAY_OF_MONTH("ERR_MONTHLY_GOAL_004", "월 목표 기간의 종료일은 %d일이어야 합니다."),
	MONTHLY_GOAL_ACCESS_DENIED("ERR_MONTHLY_GOAL_005", "해당 사용자는 접근할 수 없는 월 목표입니다."),

	// weekly-goal
	WEEKLY_GOAL_NOT_FOUND("ERR_WEEKLY_GOAL_001", "존재하지 않는 주 목표입니다."),
	INVALID_START_DAY_OF_WEEK("ERR_WEEKLY_GOAL_002", "주 목표 기간의 시작일은 일요일이어야 합니다."),
	INVALID_DIFFERENCE_OF_DATE("ERR_WEEKLY_GOAL_003", "주 목표 기간의 시작일과 종료일은 6일 차이여야 합니다."),
	WEEKLY_GOAL_ACCESS_DENIED("ERR_WEEKLY_GOAL_004", "해당 사용자는 접근할 수 없는 주 목표입니다."),

	// goal
	NULL_GOAL_TITLE("ERR_GOAL_001", "목표 제목은 null일 수 없습니다."),
	BLANK_GOAL_TITLE("ERR_GOAL_002", "목표 제목은 빈 값일 수 없습니다."),
	INVALID_GOAL_TITLE_LENGTH("ERR_GOAL_003", "목표 제목의 길이는 30자를 넘을 수 없습니다."),
	NULL_GOAL_DESCRIPTION("ERR_GOAL_004", "목표 설명은 null일 수 없습니다."),
	BLANK_GOAL_DESCRIPTION("ERR_GOAL_005", "목표 설명은 빈 값일 수 없습니다."),
	INVALID_GOAL_DESCRIPTION_LENGTH("ERR_GOAL_006", "목표 설명의 길이는 500자를 넘을 수 없습니다."),
	NULL_GOAL_COLOR_HEX("ERR_GOAL_007", "일정 색상 값은 null일 수 없습니다."),
	INVALID_GOAL_COLOR_HEX("ERR_GOAL_008", "유효하지 않은 일정 색상 값입니다."),
	NULL_GOAL_PERIOD("ERR_GOAL_009", "목표 기간 시작일과 종료일은 null일 수 없습니다."),

	// task
	TASK_NOT_FOUND("ERR_TASK_001", "존재하지 않는 할 일입니다."),
	NULL_TASK_TITLE("ERR_TASK_002", "할 일 제목은 null일 수 없습니다."),
	BLANK_TASK_TITLE("ERR_TASK_003", "할 일 제목은 빈 값일 수 없습니다."),
	INVALID_TASK_TITLE_LENGTH("ERR_TASK_004", "할 일 제목의 길이는 30자를 넘을 수 없습니다."),
	NULL_TASK_DESCRIPTION("ERR_TASK_005", "할 일 제목은 null일 수 없습니다."),
	BLANK_TASK_DESCRIPTION("ERR_TASK_006", "할 일 설명은 빈 값일 수 없습니다."),
	INVALID_TASK_DESCRIPTION_LENGTH("ERR_TASK_007", "할 일 설명의 길이는 500자를 넘을 수 없습니다."),
	NULL_TASK_COLOR_HEX("ERR_TASK_008", "할 일 생상 값은 null일 수 없습니다."),
	INVALID_TASK_COLOR_HEX("ERR_TASK_009", "유효하지 않은 할 일 색상 값입니다."),
	NULL_TASK_DATE("ERR_TASK_010", "할 일 날짜는 null일 수 없습니다."),

	// task-repetition-pattern
	TASK_REPETITION_NOT_FOUND("ERR_TASK_REPETITION_001", "존재하지 않는 할 일 반복 패턴입니다."),
	NULL_TASK_REPETITION_PERIOD("ERR_TASK_REPETITION_002", "반복 주기는 null일 수 없습니다."),
	TASK_REPETITION_PERIOD_OUT_OF_RANGE("ERR_TASK_REPETITION_003", "반복 주기는 1에서 999 사이여야 합니다."),
	NULL_TASK_REPETITION_DURATION("ERR_TASK_REPETITION_004", "반복 시작/종료일은 null일 수 없습니다."),
	TASK_REPETITION_INVALID_DURATION("ERR_TASK_REPETITION_005", "반복 시작일은 종료일 이후일 수 없습니다."),
	NULL_TASK_REPETITION_TYPE("ERR_TASK_REPETITION_006", "반복 타입은 null일 수 없습니다."),
	NULL_TASK_DAY_OF_MONTH("ERR_TASK_REPETITION_007", "dayOfMonth는 null일 수 업습니다."),
	NULL_TASK_MONTH_OF_YEAR("ERR_TASK_REPETITION_008", "monthOfYear는 null일 수 업습니다."),
	TASK_DAY_OF_MONTH_OUT_OF_RANGE("ERR_TASK_REPETITION_009", "dayOfMonth는 1과 31 사이여야 합니다."),
	TASK_MONTH_OF_YEAR_OUT_OF_RANGE("ERR_TASK_REPETITION_010", "monthOfYear는 1과 12 사이여야 합니다."),
	NULL_TASK_WEEKDAY_BIT("ERR_TASK_REPETITION_011", "요일 비트는 null일 수 없습니다."),
	TASK_WEEKDAY_BIT_OUT_OF_RANGE("ERR_TASK_REPETITION_012", "요일 비트는 1 이상 127 이하여야 합니다."),
	NULL_TASK_REPETITION_WEEK_NUMBER("ERR_TASK_REPETITION_013", "반복 주차는 null일 수 없습니다."),
	NULL_TASK_REPETITION_WEEKDAY("ERR_TASK_REPETITION_014", "반복 요일은 null일 수 없습니다."),
	NOT_EQUAL_TASK_DAY_OF_MONTH("ERR_TASK_REPETITION_015", "dayOfMonth와 startDate의 일이 일치하지 않습니다."),
	NOT_EQUAL_TASK_MONTH_OF_YEAR(
			"ERR_TASK_REPETITION_016", "monthOfYear과 startDate의 월이 일치하지 않습니다."),
	NOT_EQUAL_TASK_WEEK_NUMBER("ERR_TASK_REPETITION_017", "weekNumber와 startDate의 주차가 일치하지 않습니다."),
	NOT_EQUAL_TASK_WEEKDAY("ERR_TASK_REPETITION_017", "weekNumber와 startDate의 요일이 일치하지 않습니다."),

	// task-group
	TASK_GROUP_NOT_FOUND("ERR_TASK_GROUP_001", "존재하지 않는 할 일 그룹입니다."),

	NULL_FEED_TITLE("ERR_FEED_001", "피드 제목은 null일 수 없습니다."),
	BLANK_FEED_TITLE("ERR_FEED_002", "피드 제목은 빈 값일 수 없습니다."),
	INVALID_FEED_TITLE_LENGTH("ERR_FEED_003", "피드 제목의 길이는 30자를 넘을 수 없습니다."),
	NULL_FEED_CONTENT("ERR_FEED_004", "피드 내용은 null일 수 없습니다."),
	INVALID_FEED_CONTENT_LENGTH("ERR_FEED_005", "피드의 길이는 500자를 넘을 수 없습니다."),
	NULL_FEED_IMAGE_URL("ERR_FEED_006", "피드 이미지 url은 null일 수 없습니다."),
	BLANK_FEED_IMAGE_URL("ERR_FEED_007", "피드 이미지 url은 빈 값일 수 없습니다."),
	;

	private final String code;
	@Getter private final String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getFormattedDescription(Object... args) {
		return String.format(this.description, args);
	}
}
