package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleAlarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_alarm_id", nullable = false)
	private Long id;

	@Embedded private AlarmTime alarmTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "schedule_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Schedule schedule;

	public ScheduleAlarm(AlarmTime alarmTime, Schedule schedule) {
		this.alarmTime = alarmTime;
		this.schedule = schedule;
	}

	public LocalDateTime getAlarmTime() {
		return alarmTime.getAlarmTime();
	}
}
