package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import com.sillim.recordit.schedule.domain.vo.Location;
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
				.title(schedule.getTitle().getTitle())
				.description(schedule.getDescription().getDescription())
				.isAllDay(schedule.getScheduleDuration().getIsAllDay())
				.startDatetime(schedule.getScheduleDuration().getStartDatetime())
				.endDatetime(schedule.getScheduleDuration().getEndDatetime())
				.colorHex(schedule.getColorHex().getColorHex())
				.place(schedule.getPlace())
				.latitude(schedule.getLocation().map(Location::getLatitude).orElse(null))
				.longitude(schedule.getLocation().map(Location::getLongitude).orElse(null))
				.setAlarm(schedule.getSetAlarm())
				.alarmTime(schedule.getAlarmTime().map(AlarmTime::getAlarmTime).orElse(null))
				.isRepeated(isRepeated)
				.repetitionPattern(repetitionPatternResponse)
				.build();
	}
}
