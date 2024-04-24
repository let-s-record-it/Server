package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.schedule.domain.vo.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
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

	@Embedded private Title title;

	@Embedded private Description description;

	@Embedded private SchedulePeriod schedulePeriod;

	@Embedded private ColorHex colorHex;

	@Column(nullable = false)
	private String place;

	@Embedded private Location location;

	@Embedded private AlarmTime alarmTime;

	public Schedule(
			Title title,
			Description description,
			SchedulePeriod schedulePeriod,
			ColorHex colorHex,
			String place,
			Location location,
			AlarmTime alarmTime) {
		this.title = title;
		this.description = description;
		this.schedulePeriod = schedulePeriod;
		this.colorHex = colorHex;
		this.place = place;
		this.location = location;
		this.alarmTime = alarmTime;
	}

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
			LocalDateTime alarmTime) {
		this(new Title(title),
				new Description(description),
				SchedulePeriod.create(isAllDay, startDatetime, endDatetime),
				new ColorHex(colorHex),
				place,
				Location.create(setLocation, latitude, longitude),
				AlarmTime.create(setAlarm, alarmTime)
		);
	}

}
