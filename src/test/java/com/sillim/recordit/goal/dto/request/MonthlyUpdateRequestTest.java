package com.sillim.recordit.goal.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MonthlyUpdateRequestTest {

	@Test
	@DisplayName("MonthlyAddRequest 엔티티 매핑")
	void toEntityTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						LocalDate.of(2024, 4, 1),
						LocalDate.of(2024, 4, 30),
						1L,
						1L);
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory calendarCategory =
				CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);

		MonthlyGoal monthlyGoal = request.toEntity(category, calendar);
		assertAll(
				() -> {
					assertThat(monthlyGoal.getTitle()).isEqualTo(request.title());
					assertThat(monthlyGoal.getDescription()).isEqualTo(request.description());
					assertThat(monthlyGoal.getStartDate()).isEqualTo(request.startDate());
					assertThat(monthlyGoal.getEndDate()).isEqualTo(request.endDate());
					assertThat(monthlyGoal.isAchieved()).isEqualTo(false);
				});
	}
}
