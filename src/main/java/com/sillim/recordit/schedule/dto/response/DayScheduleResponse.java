package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record DayScheduleResponse(
		String title,
		String description,
		Boolean isAllDay,
		LocalDateTime startDatetime,
		LocalDateTime endDatetime,
		String colorHex,
		String place,
		Boolean setLocation,
		Double latitude,
		Double longitude,
		Boolean setAlarm,
		LocalDateTime alarmTime,
		Boolean isRepeated,
		RepetitionPatternResponse repetitionPattern) {

	public static DayScheduleResponse of(
			Schedule schedule,
			Boolean isRepeated,
			RepetitionPatternResponse repetitionPatternResponse) {
		return DayScheduleResponse.builder()
				.title(schedule.getTitle())
				.description(schedule.getDescription())
				.isAllDay(schedule.getIsAllDay())
				.startDatetime(schedule.getStartDatetime())
				.endDatetime(schedule.getEndDatetime())
				.colorHex(schedule.getColorHex())
				.place(schedule.getPlace())
				.latitude(schedule.getLatitude())
				.longitude(schedule.getLongitude())
				.setAlarm(schedule.getSetAlarm())
				.alarmTime(schedule.getAlarmTime())
				.isRepeated(isRepeated)
				.repetitionPattern(repetitionPatternResponse)
				.build();
	}
}
