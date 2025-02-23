package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository
		extends JpaRepository<Schedule, Long>, CustomScheduleRepository {

	@Modifying(clearAutomatically = true)
	@Query(
			"update Schedule s set s.category.id = :defaultCategoryId where s.category.id ="
					+ " :categoryId")
	int updateCategorySetDefault(
			@Param("defaultCategoryId") Long defaultCategoryId,
			@Param("categoryId") Long categoryId);

	@Modifying(clearAutomatically = true)
	@Query("update Schedule s set s.deleted = true where s.calendar.id = :calendarId")
	int deleteSchedulesInCalendar(@Param("calendarId") Long calendarId);
}
