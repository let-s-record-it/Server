package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.schedule.domain.vo.Location;
import com.sillim.recordit.schedule.domain.vo.ScheduleColorHex;
import com.sillim.recordit.schedule.domain.vo.ScheduleDescription;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.domain.vo.ScheduleTitle;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	@OneToMany(mappedBy = "schedule")
	private List<ScheduleAlarm> scheduleAlarms = new ArrayList<>();

	public Schedule(
			ScheduleTitle title,
			ScheduleDescription description,
			ScheduleDuration scheduleDuration,
			ScheduleColorHex colorHex,
			String place,
			Boolean setLocation,
			Location location,
			Boolean setAlarm,
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
				calendar,
				scheduleGroup);
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getDescription() {
		return description.getDescription();
	}

	public Boolean getIsAllDay() {
		return scheduleDuration.getIsAllDay();
	}

	public LocalDateTime getStartDatetime() {
		return scheduleDuration.getStartDatetime();
	}

	public LocalDateTime getEndDatetime() {
		return scheduleDuration.getEndDatetime();
	}

	public String getColorHex() {
		return colorHex.getColorHex();
	}

	public Double getLatitude() {
		return Optional.ofNullable(location).map(Location::getLatitude).orElse(null);
	}

	public Double getLongitude() {
		return Optional.ofNullable(location).map(Location::getLongitude).orElse(null);
	}
}
