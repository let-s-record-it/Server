package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
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
public class WeeklyGoalQueryServiceTest {

	@Mock WeeklyGoalRepository weeklyGoalRepository;
	@InjectMocks WeeklyGoalQueryService weeklyGoalQueryService;

	private Member member;
	private ScheduleCategory category;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
		category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 주 목표들을 조회한다.")
	void searchAllWeeklyGoalByDate() {
		Long memberId = 1L;
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
												member))
						.toList();
		given(weeklyGoalRepository.findWeeklyGoalInMonth(eq(year), eq(month), eq(memberId)))
				.willReturn(weeklyGoals);

		List<WeeklyGoal> found =
				weeklyGoalQueryService.searchAllWeeklyGoalByDate(year, month, memberId);

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
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoal expected = spy(WeeklyGoalFixture.DEFAULT.getWithMember(category, member));
		given(weeklyGoalRepository.findWeeklyGoalById(eq(weeklyGoalId)))
				.willReturn(Optional.of(expected));
		willDoNothing().given(expected).validateAuthenticatedMember(eq(memberId));

		WeeklyGoal found =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		assertThat(found).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("id를 기반으로 주 목표를 조회한다. - 사용자가 소유한 주 목표가 아닐 경우 InvalidRequestException이 발생한다.")
	void searchByIdAndCheckAuthorityThrowsInvalidRequestExceptionIf() {
		Long memberId = 1L;
		Long weeklyGoalId = 2L;
		WeeklyGoal expected = spy(WeeklyGoalFixture.DEFAULT.getWithMember(category, member));
		given(weeklyGoalRepository.findWeeklyGoalById(eq(weeklyGoalId)))
				.willReturn(Optional.of(expected));
		willThrow(new InvalidRequestException(ErrorCode.WEEKLY_GOAL_ACCESS_DENIED))
				.given(expected)
				.validateAuthenticatedMember(eq(memberId));

		assertThatCode(
						() -> {
							weeklyGoalQueryService.searchByIdAndCheckAuthority(
									weeklyGoalId, memberId);
						})
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.WEEKLY_GOAL_ACCESS_DENIED.getDescription());
	}
}
