package com.sillim.recordit.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyRecurrence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_recurrence_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MonthlyRecurrenceType recurrenceType;

	@Column private Integer date;

	@Column private Integer weekNumber;

	@Enumerated(EnumType.STRING)
	@Column
	private String weekday;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	@Builder
	public MonthlyRecurrence(
			MonthlyRecurrenceType recurrenceType,
			Integer date,
			Integer weekNumber,
			String weekday,
			ScheduleGroup scheduleGroup) {
		this.recurrenceType = recurrenceType;
		this.date = date;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.scheduleGroup = scheduleGroup;
	}
}
