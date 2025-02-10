package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.member.domain.Member;
import org.hibernate.validator.constraints.Length;

public record CalendarAddRequest(@Length(min = 1, max = 30) String title, Long calendarCategoryId) {

	public static final String GENERAL_CALENDAR_TITLE = "일반";

	public Calendar toCalendar(Member member, CalendarCategory category) {
		return new Calendar(title, member, category);
	}

	public static CalendarAddRequest createGeneralCalendar(Long defaultCategoryId) {
		return new CalendarAddRequest(GENERAL_CALENDAR_TITLE, defaultCategoryId);
	}
}
