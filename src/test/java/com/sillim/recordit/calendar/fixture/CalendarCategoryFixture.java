package com.sillim.recordit.calendar.fixture;

import com.sillim.recordit.calendar.domain.CalendarCategory;

public enum CalendarCategoryFixture {
	DEFAULT("aaffbb", "랜덤색", true);

	private final String colorHex;
	private final String name;
	private final boolean isDefault;

	CalendarCategoryFixture(String colorHex, String name, boolean isDefault) {
		this.colorHex = colorHex;
		this.name = name;
		this.isDefault = isDefault;
	}

	public CalendarCategory getCalendarCategory(Long memberId) {
		return new CalendarCategory(colorHex, name, isDefault, memberId);
	}
}
