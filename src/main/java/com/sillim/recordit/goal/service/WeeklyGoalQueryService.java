package com.sillim.recordit.goal.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyGoalQueryService {

	private final CalendarQueryService calendarQueryService;
	private final WeeklyGoalRepository weeklyGoalRepository;

	public List<WeeklyGoal> searchAllWeeklyGoalByDate(
			final Integer year, final Integer month, final Long memberId, final Long calendarId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(memberId);

		return weeklyGoalRepository.findWeeklyGoalInMonth(year, month, calendarId);
	}

	public WeeklyGoal searchByIdAndCheckAuthority(final Long weeklyGoalId, final Long memberId) {

		WeeklyGoal weeklyGoal =
				weeklyGoalRepository
						.findWeeklyGoalById(weeklyGoalId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.WEEKLY_GOAL_NOT_FOUND));
		weeklyGoal.validateAuthenticatedMember(memberId);

		return weeklyGoal;
	}
}
