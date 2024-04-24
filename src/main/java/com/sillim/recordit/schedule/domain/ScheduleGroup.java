package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_group_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long isRepeated;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ScheduleRepetitionType recurrentType;

	@Column(nullable = false)
	private Integer repetitionPeriod;

	@Column(nullable = false)
	private LocalDateTime repetitionStartDate;

	@Column(nullable = false)
	private LocalDateTime repetitionEndDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Member member;

	@Builder
	public ScheduleGroup(
			Long isRepeated,
			ScheduleRepetitionType recurrentType,
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Calendar calendar,
			Member member) {
		this.isRepeated = isRepeated;
		this.recurrentType = recurrentType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.calendar = calendar;
		this.member = member;
	}
}
