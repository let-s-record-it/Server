package com.sillim.recordit.schedule.service;

import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleAlarm;
import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import com.sillim.recordit.schedule.repository.ScheduleAlarmRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleAlarmService {

	private final ScheduleAlarmRepository scheduleAlarmRepository;

	@Transactional(readOnly = true)
	public List<ScheduleAlarm> searchByScheduleId(Long scheduleId) {
		return scheduleAlarmRepository.findByScheduleId(scheduleId);
	}

	public void addScheduleAlarms(List<LocalDateTime> alarmTimes, Schedule schedule) {
		alarmTimes.forEach(
				alarmTime ->
						scheduleAlarmRepository.save(
								new ScheduleAlarm(AlarmTime.create(alarmTime), schedule)));
	}
}
