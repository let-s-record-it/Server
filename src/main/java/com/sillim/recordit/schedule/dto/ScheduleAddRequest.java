package com.sillim.recordit.schedule.dto;

import com.sillim.recordit.global.validation.schedule.ValidLongitude;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record ScheduleAddRequest(
		@Length(min = 1, max = 30) String title,
		@Length(max = 500) String description,
		@NotNull Boolean isAllDay,
		@NotNull LocalDateTime startDatetime,
		@NotNull LocalDateTime endDatetime,
		@NotNull Boolean isRepeated,
		@Validated RepetitionAddRequest repetition,
		@Pattern(regexp = "[0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3}") String colorHex,
		@NotNull String place,
		@NotNull Boolean setLocation,
		@ValidLongitude Double latitude,
		@ValidLongitude Double longitude,
		@NotNull Boolean setAlarm,
		LocalDateTime alarmTime,
		@NotNull Long calendarId) {

	public Schedule toSchedule(TemporalAmount plusAmount, ScheduleGroup scheduleGroup) {
		return Schedule.builder()
				.title(title)
				.description(description)
				.isAllDay(isAllDay)
				.startDatetime(startDatetime.plus(plusAmount))
				.endDatetime(endDatetime.plus(plusAmount))
				.colorHex(colorHex)
				.place(place)
				.setLocation(setLocation)
				.latitude(latitude)
				.longitude(longitude)
				.setAlarm(setAlarm)
				.alarmTime(alarmTime)
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public Schedule toSchedule(ScheduleGroup scheduleGroup) {
		return Schedule.builder()
				.title(title)
				.description(description)
				.isAllDay(isAllDay)
				.startDatetime(startDatetime)
				.endDatetime(endDatetime)
				.colorHex(colorHex)
				.place(place)
				.setLocation(setLocation)
				.latitude(latitude)
				.longitude(longitude)
				.setAlarm(setAlarm)
				.alarmTime(alarmTime)
				.scheduleGroup(scheduleGroup)
				.build();
	}
}
