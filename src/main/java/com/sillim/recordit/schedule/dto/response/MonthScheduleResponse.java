package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MonthScheduleResponse(
		Long id,
		String title,
		boolean isAllDay,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		String colorHex) {

	public static MonthScheduleResponse from(Schedule schedule) {
		return MonthScheduleResponse.builder()
				.id(schedule.getId())
				.title(schedule.getTitle())
				.isAllDay(schedule.isAllDay())
				.startDateTime(schedule.getStartDateTime())
				.endDateTime(schedule.getEndDateTime())
				.colorHex(schedule.getColorHex())
				.build();
	}
}
