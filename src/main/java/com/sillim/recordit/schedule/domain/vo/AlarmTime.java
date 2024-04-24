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

	@Column(nullable = false)
	private Boolean setAlarm;

	@Column private LocalDateTime alarmTime;

	private AlarmTime(Boolean setAlarm, LocalDateTime alarmTime) {
		this.setAlarm = setAlarm;
		this.alarmTime = alarmTime;
	}

	public static AlarmTime noAlarmTime() {
		return new AlarmTime(false, null);
	}

	public static AlarmTime create(Boolean setAlarm, LocalDateTime alarmTime) {
		if (!setAlarm) {
			return noAlarmTime();
		}
		return new AlarmTime(true, alarmTime);
	}
}
