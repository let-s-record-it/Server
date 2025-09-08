package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyGoalUpdateServiceTest {

	@Mock
	MonthlyGoalQueryService monthlyGoalQueryService;
	@Mock
	CalendarQueryService calendarQueryService;
	@Mock
	CalendarMemberService calendarMemberService;
	@Mock
	MonthlyGoalRepository monthlyGoalRepository;
	@Mock
	ScheduleCategoryQueryService scheduleCategoryQueryService;
	@InjectMocks
	MonthlyGoalUpdateService monthlyGoalUpdateService;

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
	@DisplayName("새로운 월 목표를 추가한다.")
	void addTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		Long categoryId = 3L;
		Long calendarId = 4L;
		MonthlyGoalUpdateRequest request = new MonthlyGoalUpdateRequest("취뽀하기!", "취업할 때까지 숨 참는다.",
				LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30), categoryId, calendarId);
		MonthlyGoal saved = mock(MonthlyGoal.class);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(scheduleCategoryQueryService.searchScheduleCategory(categoryId)).willReturn(category);
		given(monthlyGoalRepository.save(any(MonthlyGoal.class))).willReturn(saved);
		given(saved.getId()).willReturn(monthlyGoalId);

		Long savedId = monthlyGoalUpdateService.add(request, memberId, calendarId);

		assertThat(savedId).isEqualTo(monthlyGoalId);
		then(monthlyGoalRepository).should(times(1)).save(any(MonthlyGoal.class));
	}

	@Test
	@DisplayName("기존의 월 목표를 수정한다.")
	void modifyTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		Long categoryId = 3L;
		Long calendarId = 4L;
		MonthlyGoalUpdateRequest request = new MonthlyGoalUpdateRequest("(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다.",
				LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31), categoryId, calendarId);
		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		given(scheduleCategoryQueryService.searchScheduleCategory(categoryId)).willReturn(category);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(eq(monthlyGoalId))).willReturn(monthlyGoal);

		monthlyGoalUpdateService.modify(request, monthlyGoalId, memberId);

		assertAll(() -> {
			assertThat(monthlyGoal.getTitle()).isEqualTo(request.title());
			assertThat(monthlyGoal.getDescription()).isEqualTo(request.description());
			assertThat(monthlyGoal.getStartDate()).isEqualTo(request.startDate());
			assertThat(monthlyGoal.getEndDate()).isEqualTo(request.endDate());
		});
	}

	@Test
	@DisplayName("월 목표의 달성 상태를 변경한다.")
	void changeAchieveStatusTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		Boolean status = true;
		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(eq(monthlyGoalId))).willReturn(monthlyGoal);

		monthlyGoalUpdateService.changeAchieveStatus(monthlyGoalId, status, memberId);

		assertThat(monthlyGoal.isAchieved()).isTrue();
	}

	@Test
	@DisplayName("해당 월 목표를 삭제한다.")
	void removeTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(eq(monthlyGoalId))).willReturn(monthlyGoal);

		monthlyGoalUpdateService.remove(monthlyGoalId, memberId);

		assertThat(monthlyGoal.isDeleted()).isTrue();
	}
}
