package com.sillim.recordit.schedule.dto.request;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.validation.common.ColorHexValid;
import com.sillim.recordit.global.validation.schedule.ValidLatitude;
import com.sillim.recordit.global.validation.schedule.ValidLongitude;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record ScheduleAddRequest(
		@Length(min = 1, max = 30) String title,
		@Length(max = 500) String description,
		@NotNull Boolean isAllDay,
		@NotNull LocalDateTime startDateTime,
		@NotNull LocalDateTime endDateTime,
		@NotNull Boolean isRepeated,
		@Validated RepetitionUpdateRequest repetition,
		@ColorHexValid String colorHex,
		@NotNull String place,
		@NotNull Boolean setLocation,
		@ValidLatitude Double latitude,
		@ValidLongitude Double longitude,
		@NotNull Boolean setAlarm,
		List<LocalDateTime> alarmTimes) {

	public Schedule toSchedule(
			TemporalAmount plusAmount, Calendar calendar, ScheduleGroup scheduleGroup) {
		return Schedule.builder()
				.title(title)
				.description(description)
				.isAllDay(isAllDay)
				.startDateTime(startDateTime.plus(plusAmount))
				.endDateTime(endDateTime.plus(plusAmount))
				.colorHex(colorHex)
				.place(place)
				.setLocation(setLocation)
				.latitude(latitude)
				.longitude(longitude)
				.setAlarm(setAlarm)
				.calendar(calendar)
				.scheduleGroup(scheduleGroup)
				.scheduleAlarms(alarmTimes)
				.build();
	}

	public Schedule toSchedule(Calendar calendar, ScheduleGroup scheduleGroup) {
		return Schedule.builder()
				.title(title)
				.description(description)
				.isAllDay(isAllDay)
				.startDateTime(startDateTime)
				.endDateTime(endDateTime)
				.colorHex(colorHex)
				.place(place)
				.setLocation(setLocation)
				.latitude(latitude)
				.longitude(longitude)
				.setAlarm(setAlarm)
				.calendar(calendar)
				.scheduleGroup(scheduleGroup)
				.scheduleAlarms(alarmTimes)
				.build();
	}
}
