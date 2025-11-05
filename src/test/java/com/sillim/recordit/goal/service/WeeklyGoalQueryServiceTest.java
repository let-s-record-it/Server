package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.domain.Member;
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
public class WeeklyGoalQueryServiceTest {

	@Mock WeeklyGoalRepository weeklyGoalRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock CalendarMemberService calendarMemberService;
	@InjectMocks WeeklyGoalQueryService weeklyGoalQueryService;

	long memberId = 1L;
	private Member member;
	private ScheduleCategory category;
	private CalendarCategory calendarCategory;
	private Calendar calendar;

	@BeforeEach
	void beforeEach() {
		member = mock(Member.class);
		calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 주 목표들을 조회한다.")
	void searchAllWeeklyGoalByDate() {
		Long calendarId = 2L;
		Integer year = 2024;
		Integer month = 8;
		List<WeeklyGoal> weeklyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) ->
										WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
												3,
												LocalDate.of(2024, 8, 11),
												LocalDate.of(2024, 8, 17),
												category,
												calendar))
						.toList();
		given(weeklyGoalRepository.findWeeklyGoalInMonth(eq(year), eq(month), eq(calendarId)))
				.willReturn(weeklyGoals);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		List<WeeklyGoal> found =
				weeklyGoalQueryService.searchAllWeeklyGoalByDate(year, month, memberId, calendarId);

		assertAll(
				() -> {
					assertThat(found).hasSize(weeklyGoals.size());
					found.forEach(wg -> assertThat(wg.getStartDate()).hasYear(year));
					found.forEach(wg -> assertThat(wg.getStartDate()).hasMonth(Month.of(month)));
				});
	}

	@Test
	@DisplayName("id를 기반으로 주 목표를 조회한다.")
	void searchByIdAndCheckAuthority() {
		Long weeklyGoalId = 2L;
		WeeklyGoal expected = spy(WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar));
		given(weeklyGoalRepository.findWeeklyGoalById(eq(weeklyGoalId)))
				.willReturn(Optional.of(expected));

		WeeklyGoal found = weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId);
		assertThat(found).usingRecursiveComparison().isEqualTo(expected);
	}
}
