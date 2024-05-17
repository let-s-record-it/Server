package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Member;

public record CalendarAddRequest(String title, String colorHex) {

	public Calendar toCalendar(Member member) {
		return Calendar.builder().title(title).colorHex(colorHex).member(member).build();
	}
}
