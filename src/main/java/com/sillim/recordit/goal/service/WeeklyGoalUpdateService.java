package com.sillim.recordit.goal.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidWeeklyGoalException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyGoalUpdateService {

	private final WeeklyGoalQueryService weeklyGoalQueryService;
	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final CalendarMemberService calendarMemberService;
	private final CalendarQueryService calendarQueryService;
	private final WeeklyGoalRepository weeklyGoalRepository;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;

	public Long addWeeklyGoal(final WeeklyGoalUpdateRequest request, final Long memberId, final Long calendarId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		validateExistsCalendarMember(calendarId, memberId);

		ScheduleCategory category = scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());
		if (request.relatedMonthlyGoalId() == null) {
			return weeklyGoalRepository.save(request.toEntity(category, calendar)).getId();
		}
		MonthlyGoal relatedMonthlyGoal = monthlyGoalQueryService
				.searchByIdAndCheckAuthority(request.relatedMonthlyGoalId());
		return weeklyGoalRepository.save(request.toEntity(category, relatedMonthlyGoal, calendar)).getId();
	}

	public void modifyWeeklyGoal(final WeeklyGoalUpdateRequest request, final Long weeklyGoalId, final Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(request.calendarId());
		validateExistsCalendarMember(calendar.getId(), memberId);
		WeeklyGoal weeklyGoal = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		ScheduleCategory category = scheduleCategoryQueryService.searchScheduleCategory(request.categoryId());

		if (request.relatedMonthlyGoalId() == null) {
			weeklyGoal.modify(request.title(), request.description(), request.week(), request.startDate(),
					request.endDate(), category, calendar);
			return;
		}
		MonthlyGoal monthlyGoal = monthlyGoalQueryService.searchByIdAndCheckAuthority(request.relatedMonthlyGoalId());
		weeklyGoal.modify(request.title(), request.description(), request.week(), request.startDate(),
				request.endDate(), category, monthlyGoal, calendar);
	}

	public void changeAchieveStatus(final Long weeklyGoalId, final Boolean status, final Long memberId) {
		WeeklyGoal weeklyGoal = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		validateExistsCalendarMember(weeklyGoal.getCalendar().getId(), memberId);

		weeklyGoal.changeAchieveStatus(status);
	}

	public void remove(final Long weeklyGoalId, final Long memberId) {
		WeeklyGoal weeklyGoal = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		validateExistsCalendarMember(weeklyGoal.getCalendar().getId(), memberId);
		weeklyGoal.remove();
	}

	public void linkRelatedMonthlyGoal(final Long weeklyGoalId, final Long monthlyGoalId, final Long memberId) {
		WeeklyGoal weeklyGoal = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		validateExistsCalendarMember(weeklyGoal.getCalendar().getId(), memberId);
		MonthlyGoal monthlyGoal = monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId);
		weeklyGoal.linkRelatedMonthlyGoal(monthlyGoal);
	}

	public void unlinkRelatedMonthlyGoal(final Long weeklyGoalId, final Long memberId) {
		WeeklyGoal weeklyGoal = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		validateExistsCalendarMember(weeklyGoal.getCalendar().getId(), memberId);
		if (weeklyGoal.getRelatedMonthlyGoal().isEmpty()) {
			throw new InvalidWeeklyGoalException(ErrorCode.RELATED_GOAL_NOT_FOUND);
		}
		weeklyGoal.unlinkRelatedMonthlyGoal();
	}

	private void validateExistsCalendarMember(Long calendarId, Long memberId) {
		calendarMemberService.searchCalendarMember(calendarId, memberId);
	}
}
