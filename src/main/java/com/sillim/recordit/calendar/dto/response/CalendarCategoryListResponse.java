package com.sillim.recordit.calendar.dto.response;

import com.sillim.recordit.calendar.domain.CalendarCategory;

public record CalendarCategoryListResponse(Long id, String colorHex, String name, boolean isDefault) {

	public static CalendarCategoryListResponse of(CalendarCategory calendarCategory) {
		return new CalendarCategoryListResponse(calendarCategory.getId(), calendarCategory.getColorHex(),
				calendarCategory.getName(), calendarCategory.isDefault());
	}
}
