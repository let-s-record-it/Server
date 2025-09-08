package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.repository.CalendarCategoryRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarCategoryQueryService {
	private final CalendarCategoryRepository calendarCategoryRepository;

	public List<CalendarCategory> searchCalendarCategories(Long memberId) {
		return calendarCategoryRepository.findByDeletedIsFalseAndMemberId(memberId);
	}

	public CalendarCategory searchCalendarCategory(Long categoryId) {
		return calendarCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.CALENDAR_CATEGORY_NOT_FOUND));
	}

	public CalendarCategory searchDefaultCategory(Long memberId) {
		return calendarCategoryRepository.findByDeletedIsFalseAndMemberIdAndIsDefaultIsTrue(memberId);
	}
}
