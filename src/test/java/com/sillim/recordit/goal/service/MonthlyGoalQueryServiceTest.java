package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyGoalQueryServiceTest {

	@Mock MonthlyGoalRepository monthlyGoalRepository;
	@InjectMocks MonthlyGoalQueryService monthlyGoalQueryService;

	private Member member;
	private ScheduleCategory category;
	private CalendarCategory calendarCategory;
	private Calendar calendar;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
		calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
	}

	@Test
	@DisplayName("id를 기반으로 월 목표를 조회한다.")
	void searchTest() {
		Long monthlyGoalId = 1L;
		MonthlyGoal expected = spy(MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar));
		given(monthlyGoalRepository.findByIdWithFetch(eq(monthlyGoalId)))
				.willReturn(Optional.of(expected));

		MonthlyGoal found = monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId);
		assertThat(found).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("id에 해당하는 월 목표가 존재하지 않을 경우 RecordNotFoundException을 발생시킨다.")
	void searchTestMonthlyGoalNotFound() {
		Long monthlyGoalId = 1L;
		given(monthlyGoalRepository.findByIdWithFetch(eq(monthlyGoalId)))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		assertThatThrownBy(() -> monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 월 목표들을 조회한다.")
	void searchAllByDateTest() {
		Long calendarId = 1L;
		Integer year = 2024;
		Integer month = 4;
		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) ->
										MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
												LocalDate.of(2024, 4, 1),
												LocalDate.of(2024, 4, 30),
												category,
												member,
												calendar))
						.toList();
		given(monthlyGoalRepository.findMonthlyGoalInMonth(eq(year), eq(month), eq(calendarId)))
				.willReturn(monthlyGoals);

		List<MonthlyGoal> found = monthlyGoalQueryService.searchAllByDate(year, month, calendarId);

		assertAll(
				() -> {
					assertThat(found).hasSize(monthlyGoals.size());
					found.forEach(mg -> assertThat(mg.getStartDate()).hasYear(year));
					found.forEach(mg -> assertThat(mg.getStartDate()).hasMonth(Month.of(month)));
				});
	}
}
