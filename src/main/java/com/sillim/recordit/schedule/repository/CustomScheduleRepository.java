package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDate;
import java.util.List;

public interface CustomScheduleRepository {

	List<Schedule> findScheduleInMonth(Long calendarId, Integer year, Integer month);

	List<Schedule> findScheduleInDay(Long calendarId, LocalDate date);
}
