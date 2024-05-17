package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

	List<Calendar> findByMemberId(Long memberId);
}
