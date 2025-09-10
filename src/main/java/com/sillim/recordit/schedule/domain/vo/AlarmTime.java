package com.sillim.recordit.schedule.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmTime {

	@Column private LocalDateTime alarmTime;

	private AlarmTime(LocalDateTime alarmTime) {
		this.alarmTime = alarmTime;
	}

	public static AlarmTime create(LocalDateTime alarmTime) {
		return new AlarmTime(alarmTime.withSecond(0));
	}
}
