package com.sillim.recordit.schedule.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleQueryService {

	private final ScheduleRepository scheduleRepository;

	public Schedule searchSchedule(Long scheduleId) {
		return scheduleRepository
				.findById(scheduleId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
	}

	public List<Schedule> searchSchedulesInMonth(Long calendarId, Integer year, Integer month) {
		return scheduleRepository.findScheduleInMonth(calendarId, year, month);
	}
}
