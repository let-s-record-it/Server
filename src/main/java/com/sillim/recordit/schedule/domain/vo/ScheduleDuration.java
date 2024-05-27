package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidScheduleDurationException;
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
public class ScheduleDuration {

	@Column(nullable = false)
	private Boolean isAllDay;

	@Column(nullable = false)
	private LocalDateTime startDatetime;

	@Column(nullable = false)
	private LocalDateTime endDatetime;

	private ScheduleDuration(
			Boolean isAllDay, LocalDateTime startDatetime, LocalDateTime endDatetime) {
		validate(startDatetime, endDatetime);
		this.isAllDay = isAllDay;
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
	}

	public static ScheduleDuration createAllDay(
			LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return new ScheduleDuration(
				true,
				LocalDateTime.of(startDatetime.toLocalDate(), LocalTime.MIN),
				LocalDateTime.of(endDatetime.toLocalDate(), LocalTime.MIN));
	}

	public static ScheduleDuration createNotAllDay(
			LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return new ScheduleDuration(
				false,
				startDatetime.withHour(0).withMinute(0).withSecond(0).withNano(0),
				endDatetime.withHour(0).withMinute(0).withSecond(0).withNano(0));
	}

	private void validate(LocalDateTime startDatetime, LocalDateTime endDatetime) {
		if (startDatetime.isAfter(endDatetime)) {
			throw new InvalidScheduleDurationException(ErrorCode.INVALID_DURATION);
		}
	}
}
