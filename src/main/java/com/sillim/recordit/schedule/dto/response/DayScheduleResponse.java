package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record DayScheduleResponse(
		Long id,
		String title,
		String description,
		boolean isAllDay,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		Long categoryId,
		String colorHex,
		String place,
		boolean setLocation,
		Double latitude,
		Double longitude,
		boolean setAlarm,
		List<LocalDateTime> alarmTimes,
		boolean isRepeated,
		Long calendarId,
		String calendarTitle,
		RepetitionPatternResponse repetitionPattern) {

	public static DayScheduleResponse of(
			Schedule schedule,
			boolean isRepeated,
			List<LocalDateTime> alarmTimes,
			RepetitionPatternResponse repetitionPatternResponse) {
		return DayScheduleResponse.builder()
				.id(schedule.getId())
				.title(schedule.getTitle())
				.description(schedule.getDescription())
				.isAllDay(schedule.isAllDay())
				.startDateTime(schedule.getStartDateTime())
				.endDateTime(schedule.getEndDateTime())
				.categoryId(schedule.getCategory().getId())
				.colorHex(schedule.getColorHex())
				.place(schedule.getPlace())
				.setLocation(schedule.isSetLocation())
				.latitude(schedule.getLatitude())
				.longitude(schedule.getLongitude())
				.setAlarm(schedule.isSetAlarm())
				.alarmTimes(alarmTimes)
				.isRepeated(isRepeated)
				.calendarId(schedule.getCalendar().getId())
				.calendarTitle(schedule.getCalendar().getTitle())
				.repetitionPattern(repetitionPatternResponse)
				.build();
	}
}
