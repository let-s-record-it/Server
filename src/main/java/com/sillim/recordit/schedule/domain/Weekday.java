package com.sillim.recordit.schedule.domain;

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

	public Integer getValue() {
		return value;
	}
}