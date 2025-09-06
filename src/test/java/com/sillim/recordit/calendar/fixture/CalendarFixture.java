package com.sillim.recordit.calendar.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;

public enum CalendarFixture {
	DEFAULT("calendar1");
	private final String title;

	CalendarFixture(String title) {
		this.title = title;
	}

	public Calendar getCalendar(CalendarCategory category, Long memberId) {
		return new Calendar(title, category, memberId);
	}
}
