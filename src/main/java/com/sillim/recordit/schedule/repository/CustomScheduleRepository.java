package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomScheduleRepository {

	Optional<Schedule> findByScheduleId(Long scheduleId);

	List<Schedule> findScheduleInMonth(Long calendarId, Integer year, Integer month);

	List<Schedule> findScheduleInDay(Long calendarId, LocalDate date);

	List<Schedule> findSchedulesInGroup(Long scheduleGroupId);

	List<Schedule> findSchedulesInGroupAfter(Long scheduleGroupId, LocalDateTime dateTime);
}
