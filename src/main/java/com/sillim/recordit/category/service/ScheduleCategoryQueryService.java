package com.sillim.recordit.category.service;

import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleCategoryQueryService {

	private final ScheduleCategoryRepository scheduleCategoryRepository;
	private final CalendarMemberService calendarMemberService;

	public List<ScheduleCategory> searchScheduleCategories(Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		return scheduleCategoryRepository.findByDeletedIsFalseAndCalendarId(calendarId);
	}

	public ScheduleCategory searchScheduleCategory(Long categoryId) {
		return scheduleCategoryRepository
				.findById(categoryId)
				.orElseThrow(
						() -> new RecordNotFoundException(ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND));
	}

	public ScheduleCategory searchDefaultCategory(Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		return scheduleCategoryRepository.findByDeletedIsFalseAndCalendarIdAndIsDefaultIsTrue(
				calendarId);
	}
}
