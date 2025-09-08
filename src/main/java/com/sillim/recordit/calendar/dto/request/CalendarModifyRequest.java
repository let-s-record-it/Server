package com.sillim.recordit.calendar.dto.request;

import org.hibernate.validator.constraints.Length;

public record CalendarModifyRequest(@Length(min = 1, max = 30) String title, Long calendarCategoryId) {
}
