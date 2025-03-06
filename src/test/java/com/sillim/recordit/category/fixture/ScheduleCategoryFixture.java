package com.sillim.recordit.category.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;

public enum ScheduleCategoryFixture {
	DEFAULT("aaffbb", "랜덤색", true);

	private final String colorHex;
	private final String name;
	private final boolean isDefault;

	ScheduleCategoryFixture(String colorHex, String name, boolean isDefault) {
		this.colorHex = colorHex;
		this.name = name;
		this.isDefault = isDefault;
	}

	public ScheduleCategory getScheduleCategory(Calendar calendar) {
		return new ScheduleCategory(colorHex, name, isDefault, calendar);
	}
}
