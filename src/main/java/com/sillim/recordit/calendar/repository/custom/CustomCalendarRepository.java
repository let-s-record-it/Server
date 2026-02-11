package com.sillim.recordit.calendar.repository.custom;

import com.sillim.recordit.calendar.domain.Calendar;
import java.util.List;
import java.util.Optional;

public interface CustomCalendarRepository {

	Optional<Calendar> findByIdWithFetchCategory(Long calendarId);

	List<Calendar> findByMemberId(Long memberId);

	void updateCategorySetDefault(Long defaultCategoryId, Long categoryId);
}
