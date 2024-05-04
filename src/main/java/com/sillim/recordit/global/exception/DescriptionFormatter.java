package com.sillim.recordit.global.exception;

public interface DescriptionFormatter {

	default String getDescription() {
		return "오버라이딩이 필요합니다.";
	}
	;

	default String getDescription(Object... args) {
		return "오버라이딩이 필요합니다.";
	}
	;
}
