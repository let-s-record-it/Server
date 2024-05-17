package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.member.domain.Member;
import org.hibernate.validator.constraints.Length;

public record CalendarAddRequest(
		@Length(min = 1, max = 30) String title, @ColorHexValid String colorHex) {

	public Calendar toCalendar(Member member) {
		return Calendar.builder().title(title).colorHex(colorHex).member(member).build();
	}
}
