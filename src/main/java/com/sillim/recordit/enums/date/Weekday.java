package com.sillim.recordit.enums.date;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;

public enum Weekday {
	MON(1),
	TUE(2),
	WED(3),
	THU(4),
	FRI(5),
	SAT(6),
	SUN(7),
	;

	private final Integer value;

	Weekday(Integer value) {
		this.value = value;
	}

	@JsonValue
	public Integer getValue() {
		return value;
	}

	public boolean hasSameWeekday(LocalDate date) {
		return value.equals(date.getDayOfWeek().getValue());
	}
}
