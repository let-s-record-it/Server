package com.sillim.recordit.calendar.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Member;

public enum CalendarFixture {
	DEFAULT("calendar1", "aabbff");
	private final String title;
	private final String colorHex;

	CalendarFixture(String title, String colorHex) {
		this.title = title;
		this.colorHex = colorHex;
	}

	public Calendar getCalendar(Member member) {
		return Calendar.builder().title(title).colorHex(colorHex).member(member).build();
	}
}
