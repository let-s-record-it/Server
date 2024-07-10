package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import com.sillim.recordit.schedule.domain.vo.Location;
import com.sillim.recordit.schedule.domain.vo.ScheduleColorHex;
import com.sillim.recordit.schedule.domain.vo.ScheduleDescription;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.domain.vo.ScheduleTitle;
import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.ColumnDefault;

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
	private boolean setLocation;

	@Embedded private Location location;

	@Column(nullable = false)
	private boolean setAlarm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	@OneToMany(
			mappedBy = "schedule",
			fetch = FetchType.LAZY,
			cascade = CascadeType.PERSIST,
			orphanRemoval = true)
	private List<ScheduleAlarm> scheduleAlarms = new ArrayList<>();

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted;

	public Schedule(
			ScheduleTitle title,
			ScheduleDescription description,
			ScheduleDuration scheduleDuration,
			ScheduleColorHex colorHex,
			String place,
			boolean setLocation,
			Location location,
			boolean setAlarm,
			Calendar calendar,
			ScheduleGroup scheduleGroup,
			List<AlarmTime> scheduleAlarms) {
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
		this.scheduleAlarms =
				scheduleAlarms.stream()
						.map(alarmTime -> new ScheduleAlarm(alarmTime, this))
						.toList();
		this.deleted = false;
	}

	@Builder
	public Schedule(
			String title,
			String description,
			boolean isAllDay,
			LocalDateTime startDateTime,
			LocalDateTime endDateTime,
			String colorHex,
			String place,
			boolean setLocation,
			Double latitude,
			Double longitude,
			boolean setAlarm,
			Calendar calendar,
			ScheduleGroup scheduleGroup,
			List<LocalDateTime> scheduleAlarms) {
		this(
				new ScheduleTitle(title),
				new ScheduleDescription(description),
				isAllDay
						? ScheduleDuration.createAllDay(startDateTime, endDateTime)
						: ScheduleDuration.createNotAllDay(startDateTime, endDateTime),
				new ScheduleColorHex(colorHex),
				place,
				setLocation,
				setLocation ? new Location(latitude, longitude) : null,
				setAlarm,
				calendar,
				scheduleGroup,
				scheduleAlarms.stream().map(AlarmTime::create).toList());
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getDescription() {
		return description.getDescription();
	}

	public boolean isAllDay() {
		return scheduleDuration.isAllDay();
	}

	public LocalDateTime getStartDateTime() {
		return scheduleDuration.getStartDateTime();
	}

	public LocalDateTime getEndDateTime() {
		return scheduleDuration.getEndDateTime();
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

	public void modify(
			String title,
			String description,
			boolean isAllDay,
			LocalDateTime startDateTime,
			LocalDateTime endDateTime,
			String colorHex,
			String place,
			boolean setLocation,
			Double latitude,
			Double longitude,
			boolean setAlarm,
			List<LocalDateTime> scheduleAlarms,
			Calendar calendar,
			ScheduleGroup scheduleGroup) {
		this.title = new ScheduleTitle(title);
		this.description = new ScheduleDescription(description);
		this.scheduleDuration =
				isAllDay
						? ScheduleDuration.createAllDay(startDateTime, endDateTime)
						: ScheduleDuration.createNotAllDay(startDateTime, endDateTime);
		this.colorHex = new ScheduleColorHex(colorHex);
		this.place = place;
		this.setLocation = setLocation;
		this.location = setLocation ? new Location(latitude, longitude) : null;
		this.setAlarm = setAlarm;
		this.scheduleAlarms =
				scheduleAlarms.stream()
						.map(AlarmTime::create)
						.map(alarmTime -> new ScheduleAlarm(alarmTime, this))
						.toList();
		this.calendar = calendar;
		this.scheduleGroup = scheduleGroup;
	}

	public void delete() {
		this.deleted = true;
	}

	public boolean isOwnedBy(Long memberId) {
		return this.calendar.isOwnedBy(memberId);
	}

	public void validateAuthenticatedMember(Long memberId) {
		if (!isOwnedBy(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}
}
