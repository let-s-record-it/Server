package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.CalendarMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarMemberRepository
		extends JpaRepository<CalendarMember, Long>, CustomCalendarMemberRepository {
	void deleteByCalendarIdAndMemberId(Long calendarId, Long memberId);
}
