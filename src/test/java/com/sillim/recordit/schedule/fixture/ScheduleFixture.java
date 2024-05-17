package com.sillim.recordit.schedule.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import java.time.LocalDateTime;

public enum ScheduleFixture {
	DEFAULT(
			"title",
			"description",
			false,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 1, 2, 0, 0),
			"aabbff",
			"서울역",
			true,
			36.0,
			127.0,
			true,
			LocalDateTime.of(2024, 1, 1, 0, 0)),
	NOT_SET_LOCATION(
			"title",
			"description",
			false,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 1, 2, 0, 0),
			"aabbff",
			"서울역",
			false,
			36.0,
			127.0,
			true,
			LocalDateTime.of(2024, 1, 1, 0, 0)),
	NOT_SET_ALARM(
			"title",
			"description",
			false,
			LocalDateTime.of(2024, 1, 1, 0, 0),
			LocalDateTime.of(2024, 1, 2, 0, 0),
			"aabbff",
			"서울역",
			true,
			36.0,
			127.0,
			false,
			LocalDateTime.of(2024, 1, 1, 0, 0)),
	;

	private final String title;
	private final String description;
	private final Boolean isAllDay;
	private final LocalDateTime startDatetime;
	private final LocalDateTime endDatetime;
	private final String colorHex;
	private final String place;
	private final Boolean setLocation;
	private final Double latitude;
	private final Double longitude;
	private final Boolean setAlarm;
	private final LocalDateTime alarmTime;

	ScheduleFixture(
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
			LocalDateTime alarmTime) {
		this.title = title;
		this.description = description;
		this.isAllDay = isAllDay;
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
		this.colorHex = colorHex;
		this.place = place;
		this.setLocation = setLocation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.setAlarm = setAlarm;
		this.alarmTime = alarmTime;
	}

	public Schedule getSchedule(ScheduleGroup scheduleGroup, Calendar calendar) {
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
				.calendar(calendar)
				.build();
	}

	public Schedule getSchedule(ScheduleGroup scheduleGroup, Calendar calendar,
			LocalDateTime startDatetime, LocalDateTime endDatetime) {
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
				.calendar(calendar)
				.build();
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getAllDay() {
		return isAllDay;
	}

	public LocalDateTime getStartDatetime() {
		return startDatetime;
	}

	public LocalDateTime getEndDatetime() {
		return endDatetime;
	}

	public String getColorHex() {
		return colorHex;
	}

	public String getPlace() {
		return place;
	}

	public Boolean getSetLocation() {
		return setLocation;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Boolean getSetAlarm() {
		return setAlarm;
	}

	public LocalDateTime getAlarmTime() {
		return alarmTime;
	}
}
