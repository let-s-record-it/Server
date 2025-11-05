package com.sillim.recordit.pushalarm.dto;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleDeleteMessage(
		Long scheduleId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long calendarId) {

	public static ScheduleDeleteMessage from(Schedule schedule) {
		return ScheduleDeleteMessage.builder()
				.scheduleId(schedule.getId())
				.startDateTime(schedule.getStartDateTime())
				.endDateTime(schedule.getEndDateTime())
				.calendarId(schedule.getCalendar().getId())
				.build();
	}
}
