package com.sillim.recordit.schedule.fixture;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import java.time.LocalDateTime;
import java.util.List;

public enum ScheduleFixture {
	DEFAULT("title", "description", false, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 1, 2, 0, 0),
			"서울역", true, 36.0, 127.0, true, List.of(LocalDateTime.of(2024, 1, 1, 0, 0))), NOT_SET_LOCATION("title",
					"description", false, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 1, 2, 0, 0), "서울역",
					false, 36.0, 127.0, true, List.of(LocalDateTime.of(2024, 1, 1, 0, 0))), NOT_SET_ALARM("title",
							"description", false, LocalDateTime.of(2024, 1, 1, 0, 0),
							LocalDateTime.of(2024, 1, 2, 0, 0), "서울역", true, 36.0, 127.0, false,
							List.of(LocalDateTime.of(2024, 1, 1, 0, 0))),;

	private final String title;
	private final String description;
	private final Boolean isAllDay;
	private final LocalDateTime startDatetime;
	private final LocalDateTime endDatetime;
	private final String place;
	private final Boolean setLocation;
	private final Double latitude;
	private final Double longitude;
	private final Boolean setAlarm;
	private final List<LocalDateTime> scheduleAlarms;

	ScheduleFixture(String title, String description, Boolean isAllDay, LocalDateTime startDatetime,
			LocalDateTime endDatetime, String place, Boolean setLocation, Double latitude, Double longitude,
			Boolean setAlarm, List<LocalDateTime> scheduleAlarms) {
		this.title = title;
		this.description = description;
		this.isAllDay = isAllDay;
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
		this.place = place;
		this.setLocation = setLocation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.setAlarm = setAlarm;
		this.scheduleAlarms = scheduleAlarms;
	}

	public Schedule getSchedule(ScheduleCategory category, ScheduleGroup scheduleGroup, Calendar calendar) {
		return Schedule.builder().title(title).description(description).isAllDay(isAllDay).startDateTime(startDatetime)
				.endDateTime(endDatetime).place(place).setLocation(setLocation).latitude(latitude).longitude(longitude)
				.setAlarm(setAlarm).category(category).scheduleGroup(scheduleGroup).calendar(calendar)
				.scheduleAlarms(scheduleAlarms).build();
	}

	public Schedule getSchedule(ScheduleCategory category, ScheduleGroup scheduleGroup, Calendar calendar,
			LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return Schedule.builder().title(title).description(description).isAllDay(isAllDay).startDateTime(startDatetime)
				.endDateTime(endDatetime).place(place).setLocation(setLocation).latitude(latitude).longitude(longitude)
				.setAlarm(setAlarm).category(category).scheduleGroup(scheduleGroup).calendar(calendar)
				.scheduleAlarms(scheduleAlarms).build();
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
}
