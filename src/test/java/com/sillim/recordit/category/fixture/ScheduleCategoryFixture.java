package com.sillim.recordit.category.fixture;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.member.domain.Member;

public enum ScheduleCategoryFixture {
	DEFAULT("aaffbb", "랜덤색");

	private final String colorHex;
	private final String name;

	ScheduleCategoryFixture(String colorHex, String name) {
		this.colorHex = colorHex;
		this.name = name;
	}

	public ScheduleCategory getScheduleCategory(Member member) {
		return new ScheduleCategory(colorHex, name, member);
	}
}
