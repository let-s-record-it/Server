package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.member.domain.Member;
import org.hibernate.validator.constraints.Length;

public record CalendarAddRequest(
		@Length(min = 1, max = 30) String title, @ColorHexValid String colorHex) {

	public static final String GENERAL_CALENDAR_TITLE = "일반";
	public static final String GENERAL_CALENDAR_COLOR_HEX = "ff40d974";

	public Calendar toCalendar(Member member) {
		return Calendar.builder().title(title).colorHex(colorHex).member(member).build();
	}

	public static CalendarAddRequest createGeneralCalendar() {
		return new CalendarAddRequest(GENERAL_CALENDAR_TITLE, GENERAL_CALENDAR_COLOR_HEX);
	}
}
