package com.sillim.recordit.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyRepetition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "weekly_recurrence_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Boolean monday;

	@Column(nullable = false)
	private Boolean tuesday;

	@Column(nullable = false)
	private Boolean wednesday;

	@Column(nullable = false)
	private Boolean thursday;

	@Column(nullable = false)
	private Boolean friday;

	@Column(nullable = false)
	private Boolean saturday;

	@Column(nullable = false)
	private Boolean sunday;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	@Builder
	public WeeklyRepetition(
			Boolean monday,
			Boolean tuesday,
			Boolean wednesday,
			Boolean thursday,
			Boolean friday,
			Boolean saturday,
			Boolean sunday,
			ScheduleGroup scheduleGroup) {
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
		this.sunday = sunday;
		this.scheduleGroup = scheduleGroup;
	}
}
