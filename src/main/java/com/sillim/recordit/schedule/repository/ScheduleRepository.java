package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository
		extends JpaRepository<Schedule, Long>, CustomScheduleRepository {}
