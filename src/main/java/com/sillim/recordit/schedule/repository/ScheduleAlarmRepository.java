package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.ScheduleAlarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleAlarmRepository extends JpaRepository<ScheduleAlarm, Long> {

    public List<ScheduleAlarm> findByScheduleId(Long scheduleId);
}
