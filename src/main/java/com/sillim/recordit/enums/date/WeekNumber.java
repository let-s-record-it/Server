package com.sillim.recordit.enums.date;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.time.Period;

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

	public boolean contains(final LocalDate date) {
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		return date.isAfter(firstDayOfMonth.plus(Period.ofDays(7 * (value - 1) - 1)))
				&& date.isBefore(firstDayOfMonth.plus(Period.ofDays(7 * (value))));
	}
}
