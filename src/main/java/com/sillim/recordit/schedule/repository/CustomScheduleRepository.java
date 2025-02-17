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

	List<Schedule> findGroupSchedules(Long scheduleGroupId);

	List<Schedule> findGroupSchedulesAfterCurrent(Long scheduleGroupId, LocalDateTime dateTime);

	List<Schedule> findScheduleMatchedQuery(String query, Long calendarId);
}
