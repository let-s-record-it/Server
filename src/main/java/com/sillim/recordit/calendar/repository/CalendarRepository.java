package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.repository.custom.CustomCalendarRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository
		extends JpaRepository<Calendar, Long>, CustomCalendarRepository {}
