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
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeeklyGoalTest {

	long memberId = 1L;
	Member member = MemberFixture.DEFAULT.getMember();
	CalendarCategory calendarCategory =
			CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
	Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
	ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);

	@Test
	@DisplayName("주 목표를 수정할 수 있다.")
	void modify() {
		WeeklyGoal expected = WeeklyGoalFixture.MODIFIED.getWithMember(category, calendar);

		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		modified.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getWeek(),
				expected.getStartDate(),
				expected.getEndDate(),
				category,
				calendar);

		assertThat(modified).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("주 목표의 달성 상태를 변경할 수 있다.")
	void changeAchieveStatusTrue() {

		WeeklyGoal weeklyGoal1 = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		weeklyGoal1.changeAchieveStatus(true);
		WeeklyGoal weeklyGoal2 = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		weeklyGoal2.changeAchieveStatus(false);

		assertAll(
				() -> {
					assertThat(weeklyGoal1.isAchieved()).isTrue();
					assertThat(weeklyGoal2.isAchieved()).isFalse();
				});
	}

	@Test
	@DisplayName("주 목표를 삭제할 경우 deleted 플래그가 true로 변경된다.")
	void remove() {

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		weeklyGoal.remove();

		assertThat(weeklyGoal.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("주 목표와 월 목표를 연결할 수 있다.")
	void linkRelatedMonthlyGoal() {

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		MonthlyGoal relatedMonthlyGoal =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		weeklyGoal.linkRelatedMonthlyGoal(relatedMonthlyGoal);

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isNotEmpty();
		assertThat(weeklyGoal.getRelatedMonthlyGoal().get())
				.usingRecursiveComparison()
				.isEqualTo(relatedMonthlyGoal);
	}

	@Test
	@DisplayName("주 목표의 연관 목표를 연결 해제 할 수 있다.")
	void unlinkRelatedMonthlyGoal() {

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		MonthlyGoal relatedMonthlyGoal =
				MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		weeklyGoal.linkRelatedMonthlyGoal(relatedMonthlyGoal);

		weeklyGoal.unlinkRelatedMonthlyGoal();

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isEmpty();
	}
}
