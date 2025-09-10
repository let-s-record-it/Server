package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.CalendarMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarMemberRepository
		extends JpaRepository<CalendarMember, Long>, CustomCalendarMemberRepository {

	@Modifying(clearAutomatically = true)
	@Query("update CalendarMember cm set cm.deleted = true where cm.calendar.id = :calendarId")
	int deleteCalendarMembersInCalendar(@Param("calendarId") Long calendarId);

	boolean existsByCalendarIdAndMemberId(Long calendarId, Long memberId);
}
