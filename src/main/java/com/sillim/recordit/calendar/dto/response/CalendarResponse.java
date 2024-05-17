package com.sillim.recordit.calendar.dto.response;

import com.sillim.recordit.calendar.domain.Calendar;

public record CalendarResponse(Long id, String title, String colorHex) {

	public static CalendarResponse from(Calendar calendar) {
		return new CalendarResponse(calendar.getId(), calendar.getTitle(), calendar.getColorHex());
	}
}
