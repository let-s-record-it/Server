package com.sillim.recordit.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeeklyGoalTest {

	Member member = MemberFixture.DEFAULT.getMember();

	@Test
	@DisplayName("주 목표의 소유자를 검증할 때, 소유자라면 예외가 발생하지 않는다.")
	void validateAuthenticatedMember() {

		member = mock(Member.class);
		willReturn(true).given(member).equalsId(anyLong());

		WeeklyGoal weeklyG = WeeklyGoalFixture.DEFAULT.getWithMember(member);

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

		WeeklyGoal weeklyGoal = WeeklyGoalFixture.DEFAULT.getWithMember(member);

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
		WeeklyGoal expected = WeeklyGoalFixture.MODIFIED.getWithMember(member);

		WeeklyGoal modified = WeeklyGoalFixture.DEFAULT.getWithMember(member);
		modified.modify(
				expected.getTitle(),
				expected.getDescription(),
				expected.getWeek(),
				expected.getStartDate(),
				expected.getEndDate(),
				expected.getColorHex());

		assertThat(modified).usingRecursiveComparison().isEqualTo(expected);
	}
}
