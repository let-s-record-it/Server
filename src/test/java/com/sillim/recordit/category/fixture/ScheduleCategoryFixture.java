package com.sillim.recordit.category.fixture;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.member.domain.Member;

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

	public ScheduleCategory getScheduleCategory(Member member) {
		return new ScheduleCategory(colorHex, name, isDefault, member);
	}
}
