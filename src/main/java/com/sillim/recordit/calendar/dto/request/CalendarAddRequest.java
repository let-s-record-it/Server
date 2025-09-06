package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import org.hibernate.validator.constraints.Length;

public record CalendarAddRequest(@Length(min = 1, max = 30) String title, Long calendarCategoryId) {

	public static final String GENERAL_CALENDAR_TITLE = "일반";

	public Calendar toCalendar(CalendarCategory category, Long memberId) {
		return new Calendar(title, category, memberId);
	}

	public static CalendarAddRequest createGeneralCalendar(Long defaultCategoryId) {
		return new CalendarAddRequest(GENERAL_CALENDAR_TITLE, defaultCategoryId);
	}
}
