package com.sillim.recordit.goal.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyGoalUpdateService {

	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final CalendarMemberService calendarMemberService;
	private final CalendarQueryService calendarQueryService;
	private final MonthlyGoalRepository monthlyGoalRepository;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;

	public Long add(final MonthlyGoalUpdateRequest request, final Long memberId, final Long calendarId) {
		ScheduleCategory category = scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		validateExistsCalendarMember(calendar.getId(), memberId);

		MonthlyGoal monthlyGoal = monthlyGoalRepository.save(request.toEntity(category, calendar));
		return monthlyGoal.getId();
	}

	public void modify(final MonthlyGoalUpdateRequest request, final Long monthlyGoalId, final Long memberId) {
		MonthlyGoal monthlyGoal = monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId);
		validateExistsCalendarMember(monthlyGoal.getCalendar().getId(), memberId);
		ScheduleCategory category = scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		Calendar calendar = calendarQueryService.searchByCalendarId(request.calendarId());
		validateExistsCalendarMember(calendar.getId(), memberId);
		monthlyGoal.modify(request.title(), request.description(), request.startDate(), request.endDate(), category,
				calendar);
	}

	public void changeAchieveStatus(final Long monthlyGoalId, final Boolean status, final Long memberId) {
		MonthlyGoal monthlyGoal = monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId);
		validateExistsCalendarMember(monthlyGoal.getCalendar().getId(), memberId);
		monthlyGoal.changeAchieveStatus(status);
	}

	public void remove(final Long monthlyGoalId, final Long memberId) {
		MonthlyGoal monthlyGoal = monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId);
		validateExistsCalendarMember(monthlyGoal.getCalendar().getId(), memberId);
		monthlyGoal.remove();
	}

	private void validateExistsCalendarMember(Long calendarId, Long memberId) {
		calendarMemberService.searchCalendarMember(calendarId, memberId);
	}
}
