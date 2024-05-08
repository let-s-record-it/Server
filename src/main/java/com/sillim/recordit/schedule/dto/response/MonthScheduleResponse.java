package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MonthScheduleResponse(
		Long id,
		Boolean isAllDay,
		LocalDateTime startDatetime,
		LocalDateTime endDatetime,
		String colorHex) {

	public static MonthScheduleResponse from(Schedule schedule) {
		return MonthScheduleResponse.builder()
				.id(schedule.getId())
				.isAllDay(schedule.getIsAllDay())
				.startDatetime(schedule.getStartDatetime())
				.endDatetime(schedule.getEndDatetime())
				.colorHex(schedule.getColorHex())
				.build();
	}
}
