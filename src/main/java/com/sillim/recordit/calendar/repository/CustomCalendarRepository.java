package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import java.util.List;
import java.util.Optional;

public interface CustomCalendarRepository {

	Optional<Calendar> findByIdWithFetchCategory(Long calendarId);

	List<Calendar> findByMemberId(Long memberId);

	void updateMemberIsNull(Long memberId);
}
