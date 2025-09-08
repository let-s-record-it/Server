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
	private boolean isAllDay;

	@Column(nullable = false)
	private LocalDateTime startDateTime;

	@Column(nullable = false)
	private LocalDateTime endDateTime;

	private ScheduleDuration(boolean isAllDay, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		validate(startDateTime, endDateTime);
		this.isAllDay = isAllDay;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public static ScheduleDuration createAllDay(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return new ScheduleDuration(true, LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.MIN),
				LocalDateTime.of(endDateTime.toLocalDate(), LocalTime.MIN));
	}

	public static ScheduleDuration createNotAllDay(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return new ScheduleDuration(false, startDateTime.withSecond(0).withNano(0),
				endDateTime.withSecond(0).withNano(0));
	}

	private void validate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		if (startDateTime.isAfter(endDateTime)) {
			throw new InvalidScheduleDurationException(ErrorCode.INVALID_DURATION);
		}
	}
}
