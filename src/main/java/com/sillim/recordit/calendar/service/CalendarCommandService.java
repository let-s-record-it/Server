package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarModifyRequest;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.category.service.ScheduleCategoryCommandService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CalendarCommandService {

	private final CalendarRepository calendarRepository;
	private final MemberQueryService memberQueryService;
	private final CalendarQueryService calendarQueryService;
	private final CalendarMemberService calendarMemberService;
	private final CalendarCategoryQueryService calendarCategoryQueryService;
	private final ScheduleCommandService scheduleCommandService;
	private final ScheduleCategoryCommandService scheduleCategoryCommandService;

	public Calendar addCalendar(CalendarAddRequest request, Long memberId) {
		CalendarCategory calendarCategory =
				calendarCategoryQueryService.searchCalendarCategory(request.calendarCategoryId());
		Calendar calendar =
				calendarRepository.save(
						request.toCalendar(
								memberQueryService.findByMemberId(memberId), calendarCategory));
		calendarMemberService.addCalendarMember(calendar.getId(), memberId);
		scheduleCategoryCommandService.addInitialCategories(calendar.getId(), memberId);
		return calendar;
	}

	public void modifyCalendar(CalendarModifyRequest request, Long calendarId, Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		if (!calendar.isOwnedBy(memberId)) {
			throw new InvalidCalendarException(ErrorCode.INVALID_CALENDAR_GET_REQUEST);
		}

		calendar.modify(
				request.title(),
				calendarCategoryQueryService.searchCalendarCategory(request.calendarCategoryId()));
	}

	public void removeByCalendarId(Long calendarId, Long ownerId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		if (!calendar.isOwnedBy(ownerId)) {
			throw new InvalidCalendarException(ErrorCode.INVALID_CALENDAR_GET_REQUEST);
		}

		calendarMemberService.removeCalendarMembersInCalendar(calendarId, ownerId);
		scheduleCommandService.removeSchedulesInCalendar(calendarId);
		calendar.delete();
	}

	public void replaceCalendarCategoriesWithDefault(Long categoryId, Long memberId) {
		CalendarCategory defaultCategory =
				calendarCategoryQueryService.searchDefaultCategory(memberId);
		calendarRepository.updateCategorySetDefault(defaultCategory.getId(), categoryId);
	}
}
