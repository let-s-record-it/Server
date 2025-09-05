package com.sillim.recordit.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MonthlyGoalTest {

	long memberId = 1L;
	Member member = MemberFixture.DEFAULT.getMember();
	CalendarCategory calendarCategory =
			CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
	Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
	ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);

	@Test
	@DisplayName("월 목표를 수정할 수 있다.")
	void modify() {
		MonthlyGoal expected = MonthlyGoalFixture.MODIFIED.getWithMember(category, calendar);

		MonthlyGoal modified = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
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

		MonthlyGoal monthlyGoal1 = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		monthlyGoal1.changeAchieveStatus(true);
		MonthlyGoal monthlyGoal2 = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		monthlyGoal2.changeAchieveStatus(false);

		assertAll(
				() -> {
					assertThat(monthlyGoal1.isAchieved()).isTrue();
					assertThat(monthlyGoal2.isAchieved()).isFalse();
				});
	}
}
