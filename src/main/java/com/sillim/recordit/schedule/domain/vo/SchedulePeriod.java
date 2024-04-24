package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidSchedulePeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePeriod {

	@Column(nullable = false)
	private Boolean isAllDay;

	@Column(nullable = false)
	private LocalDateTime startDatetime;

	@Column(nullable = false)
	private LocalDateTime endDatetime;

	private SchedulePeriod(
			Boolean isAllDay, LocalDateTime startDatetime, LocalDateTime endDatetime) {
		validate(startDatetime, endDatetime);
		this.isAllDay = isAllDay;
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
	}

	public static SchedulePeriod createAllDay(
			LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return new SchedulePeriod(
				true,
				LocalDateTime.of(startDatetime.toLocalDate(), LocalTime.MIN),
				LocalDateTime.of(endDatetime.toLocalDate(), LocalTime.MIN));
	}

	public static SchedulePeriod createNotAllDay(LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return new SchedulePeriod(false, startDatetime, endDatetime);
	}

	public static SchedulePeriod create(Boolean isAllDay, LocalDateTime startDatetime,
			LocalDateTime endDatetime) {
		if (isAllDay) {
			return createAllDay(startDatetime, endDatetime);
		}
		return createNotAllDay(startDatetime, endDatetime);
	}

	private void validate(LocalDateTime startDatetime, LocalDateTime endDatetime) {
		if (startDatetime.isAfter(endDatetime)) {
			throw new InvalidSchedulePeriodException(
					ErrorCode.INVALID_SCHEDULE_PERIOD, "시작시간은 종료시간보다 클 수 없습니다.");
		}
	}
}
