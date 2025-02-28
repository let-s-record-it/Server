package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarMember;
import java.util.List;
import java.util.Optional;

public interface CustomCalendarMemberRepository {

	Optional<CalendarMember> findCalendarMember(Long calendarId, Long memberId);

	List<CalendarMember> findCalendarMembers(Long calendarId);

	List<Calendar> findCalendarsByMemberId(Long memberId);

	void updateMemberIsNull(Long memberId);
}
