package com.sillim.recordit.calendar.fixture;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.member.domain.Member;

public enum CalendarCategoryFixture {
	DEFAULT("aaffbb", "랜덤색");

	private final String colorHex;
	private final String name;

	CalendarCategoryFixture(String colorHex, String name) {
		this.colorHex = colorHex;
		this.name = name;
	}

	public CalendarCategory getCalendarCategory(Member member) {
		return new CalendarCategory(colorHex, name, member);
	}
}
