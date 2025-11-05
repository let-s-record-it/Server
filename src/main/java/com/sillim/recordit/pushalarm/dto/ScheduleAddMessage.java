package com.sillim.recordit.pushalarm.dto;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleAddMessage(
		Long id,
		String title,
		boolean isAllDay,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		String colorHex,
		Long calendarId) {

	public static ScheduleAddMessage from(Schedule schedule) {
		return ScheduleAddMessage.builder()
				.id(schedule.getId())
				.title(schedule.getTitle())
				.isAllDay(schedule.isAllDay())
				.startDateTime(schedule.getStartDateTime())
				.endDateTime(schedule.getEndDateTime())
				.colorHex(schedule.getColorHex())
				.calendarId(schedule.getCalendar().getId())
				.build();
	}
}
