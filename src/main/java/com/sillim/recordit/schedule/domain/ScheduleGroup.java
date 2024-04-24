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
	private Long isRecurred;

	@Column(nullable = false)
	private String recurrentType;

	@Column(nullable = false)
	private Integer recurrencePeriod;

	@Column(nullable = false)
	private LocalDateTime recurrenceStartDate;

	@Column(nullable = false)
	private LocalDateTime recurrenceEndDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Member member;

	@Builder
	public ScheduleGroup(
			Long isRecurred,
			String recurrentType,
			Integer recurrencePeriod,
			LocalDateTime recurrenceStartDate,
			LocalDateTime recurrenceEndDate,
			Calendar calendar,
			Member member) {
		this.isRecurred = isRecurred;
		this.recurrentType = recurrentType;
		this.recurrencePeriod = recurrencePeriod;
		this.recurrenceStartDate = recurrenceStartDate;
		this.recurrenceEndDate = recurrenceEndDate;
		this.calendar = calendar;
		this.member = member;
	}
}
