package com.sillim.recordit.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeeklyGoalTest {

	Member member = MemberFixture.DEFAULT.getMember();
	ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);

	@Test
	@DisplayName("주 목표의 소유자를 검증할 때, 소유자라면 예외가 발생하지 않는다.")
	void validateAuthenticatedMember() {

		member = mock(Member.class);
		willReturn(true).given(member).equalsId(anyLong());

		WeeklyGoal weeklyG = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);

		assertThatCode(
						() -> {
							weeklyG.validateAuthenticatedMember(1L);
						})
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("주 목표의 소유자를 검증할 때, 소유자가 아니라면 InvalidRequestException을 발생시킨다.")
	void validateAuthenticatedMemberThrowsInvalidRequestExceptionIfNotOwnedByMember() {

		member = mock(Member.class);
		willReturn(false).given(member).equalsId(anyLong());

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);

		assertThatCode(
						() -> {
							weeklyGoal.validateAuthenticatedMember(1L);
						})
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.WEEKLY_GOAL_ACCESS_DENIED.getDescription());
	}

	@Test
	@DisplayName("주 목표를 수정할 수 있다.")
	void modify() {
		WeeklyGoal expected = WeeklyGoalFixture.MODIFIED.getWithMember(category, member);

		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		modified.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getWeek(),
				expected.getStartDate(),
				expected.getEndDate(),
				category);

		assertThat(modified).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("주 목표의 달성 상태를 변경할 수 있다.")
	void changeAchieveStatusTrue() {

		WeeklyGoal weeklyGoal1 = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		weeklyGoal1.changeAchieveStatus(true);
		WeeklyGoal weeklyGoal2 = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
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

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		weeklyGoal.remove();

		assertThat(weeklyGoal.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("주 목표와 월 목표를 연결할 수 있다.")
	void linkRelatedMonthlyGoal() {

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		MonthlyGoal relatedMonthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, member);
		weeklyGoal.linkRelatedMonthlyGoal(relatedMonthlyGoal);

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isNotEmpty();
		assertThat(weeklyGoal.getRelatedMonthlyGoal().get())
				.usingRecursiveComparison()
				.isEqualTo(relatedMonthlyGoal);
	}

	@Test
	@DisplayName("주 목표의 연관 목표를 연결 해제 할 수 있다.")
	void unlinkRelatedMonthlyGoal() {

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(category, member);
		MonthlyGoal relatedMonthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(category, member);
		weeklyGoal.linkRelatedMonthlyGoal(relatedMonthlyGoal);

		weeklyGoal.unlinkRelatedMonthlyGoal();

		assertThat(weeklyGoal.getRelatedMonthlyGoal()).isEmpty();
	}
}
