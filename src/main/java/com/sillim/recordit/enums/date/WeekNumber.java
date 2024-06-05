package com.sillim.recordit.enums.date;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WeekNumber {
	FIRST(1),
	SECOND(2),
	THIRD(3),
	FOURTH(4),
	FIFTH(5),
	;
	private final Integer value;

	WeekNumber(Integer value) {
		this.value = value;
	}

	@JsonValue
	public Integer getValue() {
		return value;
	}
}
