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
		Boolean isAllDay,
		LocalDateTime startDatetime,
		LocalDateTime endDatetime,
		String colorHex,
		String place,
		Boolean setLocation,
		Double latitude,
		Double longitude,
		Boolean setAlarm,
		List<LocalDateTime> alarmTimes,
		Boolean isRepeated,
		String calendarTitle,
		RepetitionPatternResponse repetitionPattern) {

	public static DayScheduleResponse of(
			Schedule schedule,
			Boolean isRepeated,
			List<LocalDateTime> alarmTimes,
			RepetitionPatternResponse repetitionPatternResponse) {
		return DayScheduleResponse.builder()
				.id(schedule.getId())
				.title(schedule.getTitle())
				.description(schedule.getDescription())
				.isAllDay(schedule.getIsAllDay())
				.startDatetime(schedule.getStartDatetime())
				.endDatetime(schedule.getEndDatetime())
				.colorHex(schedule.getColorHex())
				.place(schedule.getPlace())
				.setLocation(schedule.getSetLocation())
				.latitude(schedule.getLatitude())
				.longitude(schedule.getLongitude())
				.setAlarm(schedule.getSetAlarm())
				.alarmTimes(alarmTimes)
				.isRepeated(isRepeated)
				.calendarTitle(schedule.getCalendar().getTitle())
				.repetitionPattern(repetitionPatternResponse)
				.build();
	}
}
