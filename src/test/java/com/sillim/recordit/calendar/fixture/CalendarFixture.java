package com.sillim.recordit.calendar.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.member.domain.Member;

public enum CalendarFixture {
	DEFAULT("calendar1");
	private final String title;

	CalendarFixture(String title) {
		this.title = title;
	}

	public Calendar getCalendar(Member member, CalendarCategory category) {
		return new Calendar(title, member, category);
	}
}
