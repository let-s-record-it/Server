package com.sillim.recordit.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YearlyRecurrence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "yearly_recurrence_id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private YearlyRecurrenceType recurrenceType;

	@Column(nullable = false)
	private Integer month;

	@Column private Integer date;

	@Column private Integer weekNumber;

	@Column private Integer weekday;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id")
	private ScheduleGroup scheduleGroup;

	@Builder
	public YearlyRecurrence(
			YearlyRecurrenceType recurrenceType,
			Integer month,
			Integer date,
			Integer weekNumber,
			Integer weekday,
			ScheduleGroup scheduleGroup) {
		this.recurrenceType = recurrenceType;
		this.month = month;
		this.date = date;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.scheduleGroup = scheduleGroup;
	}
}
