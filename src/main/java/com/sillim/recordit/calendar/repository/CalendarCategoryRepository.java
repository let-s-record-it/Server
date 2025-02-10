package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {

	List<CalendarCategory> findByMemberId(Long memberId);
}
