package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.schedule.domain.vo.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_id", nullable = false)
	private Long id;

	@Embedded private ScheduleTitle title;

	@Embedded private ScheduleDescription description;

	@Embedded private ScheduleDuration scheduleDuration;

	@Embedded private ScheduleColorHex colorHex;

	@Column(nullable = false)
	private String place;

	@Column(nullable = false)
	private Boolean setLocation;

	@Embedded private Location location;

	@Column(nullable = false)
	private Boolean setAlarm;

	@Embedded private AlarmTime alarmTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	public Schedule(
			ScheduleTitle title,
			ScheduleDescription description,
			ScheduleDuration scheduleDuration,
			ScheduleColorHex colorHex,
			String place,
			Boolean setLocation,
			Location location,
			Boolean setAlarm,
			AlarmTime alarmTime,
			Calendar calendar,
			ScheduleGroup scheduleGroup) {
		this.title = title;
		this.description = description;
		this.scheduleDuration = scheduleDuration;
		this.colorHex = colorHex;
		this.place = place;
		this.setLocation = setLocation;
		this.location = location;
		this.setAlarm = setAlarm;
		this.alarmTime = alarmTime;
		this.calendar = calendar;
		this.scheduleGroup = scheduleGroup;
	}

	@Builder
	public Schedule(
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
			Calendar calendar,
			ScheduleGroup scheduleGroup) {
		this(
				new ScheduleTitle(title),
				new ScheduleDescription(description),
				isAllDay
						? ScheduleDuration.createAllDay(startDatetime, endDatetime)
						: ScheduleDuration.createNotAllDay(startDatetime, endDatetime),
				new ScheduleColorHex(colorHex),
				place,
				setLocation,
				setLocation ? new Location(latitude, longitude) : null,
				setAlarm,
				setAlarm ? AlarmTime.create(alarmTime) : null,
				calendar,
				scheduleGroup);
	}

	public Optional<Location> getLocation() {
		return Optional.ofNullable(this.location);
	}

	public Optional<AlarmTime> getAlarmTime() {
		return Optional.ofNullable(this.alarmTime);
	}
}
