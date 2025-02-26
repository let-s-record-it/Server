package com.sillim.recordit.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MonthlyGoalTest {

	Member member = MemberFixture.DEFAULT.getMember();
	ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
	CalendarCategory calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
	Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);

	@Test
	@DisplayName("월 목표를 수정할 수 있다.")
	void modify() {
		MonthlyGoal expected =
				MonthlyGoalFixture.MODIFIED.getWithMember(category, member, calendar);

		MonthlyGoal modified = MonthlyGoalFixture.DEFAULT.getWithMember(category, member, calendar);
		modified.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getStartDate(),
				expected.getEndDate(),
				category,
				calendar);

		assertThat(modified).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("월 목표의 달성 상태를 변경할 수 있다.")
	void changeAchieveStatusTrue() {

		MonthlyGoal monthlyGoal1 =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, member, calendar);
		monthlyGoal1.changeAchieveStatus(true);
		MonthlyGoal monthlyGoal2 =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, member, calendar);
		monthlyGoal2.changeAchieveStatus(false);

		assertAll(
				() -> {
					assertThat(monthlyGoal1.isAchieved()).isTrue();
					assertThat(monthlyGoal2.isAchieved()).isFalse();
				});
	}

	@Test
	@DisplayName("월 목표의 소유자를 검증할 때, 소유자라면 예외가 발생하지 않는다.")
	void validateAuthenticatedMember() {

		member = mock(Member.class);
		willReturn(true).given(member).equalsId(anyLong());

		MonthlyGoal monthlyGoal =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, member, calendar);

		assertThatCode(
						() -> {
							monthlyGoal.validateAuthenticatedMember(1L);
						})
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("월 목표의 소유자를 검증할 때, 소유자가 아니라면 InvalidRequestException을 발생시킨다.")
	void validateAuthenticatedMemberThrowsInvalidRequestExceptionIfNotOwnedByMember() {

		member = mock(Member.class);
		willReturn(false).given(member).equalsId(anyLong());

		MonthlyGoal monthlyGoal =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, member, calendar);

		assertThatCode(
						() -> {
							monthlyGoal.validateAuthenticatedMember(1L);
						})
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_ACCESS_DENIED.getDescription());
	}
}
