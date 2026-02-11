package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.repository.custom.CustomCalendarMemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarMemberRepository
		extends JpaRepository<CalendarMember, Long>, CustomCalendarMemberRepository {

	boolean existsByDeletedIsFalseAndCalendarIdAndMemberId(Long calendarId, Long memberId);
}
