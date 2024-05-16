package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.Schedule;
import java.util.List;

public interface CustomScheduleRepository {

    List<Schedule> findScheduleInMonth(Long calendarId, Integer year, Integer month);
}
