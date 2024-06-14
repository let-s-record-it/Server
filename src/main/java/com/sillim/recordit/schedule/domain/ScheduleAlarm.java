package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleAlarm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_alarm_id", nullable = false)
	private Long id;

	@Embedded private AlarmTime alarmTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id")
	private Schedule schedule;

	public ScheduleAlarm(AlarmTime alarmTime, Schedule schedule) {
		this.alarmTime = alarmTime;
		this.schedule = schedule;
	}

	public LocalDateTime getAlarmTime() {
		return alarmTime.getAlarmTime();
	}
}
