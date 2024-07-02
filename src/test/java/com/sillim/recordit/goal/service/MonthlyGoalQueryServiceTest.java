package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.global.exception.goal.InvalidMonthlyGoalException;
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

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("id를 기반으로 월 목표를 조회한다.")
	void searchTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal expected = spy(MonthlyGoalFixture.DEFAULT.getWithMember(member));
		given(monthlyGoalRepository.findById(eq(monthlyGoalId))).willReturn(Optional.of(expected));
		willReturn(true).given(expected).isOwnedBy(eq(memberId));

		MonthlyGoal found = monthlyGoalQueryService.searchById(monthlyGoalId, memberId);
		assertThat(found).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("id에 해당하는 월 목표가 존재하지 않을 경우 RecordNotFoundException을 발생시킨다.")
	void searchTestMonthlyGoalNotFound() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		given(monthlyGoalRepository.findById(eq(monthlyGoalId)))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		assertThatThrownBy(() -> monthlyGoalQueryService.searchById(monthlyGoalId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("월 목표가 해당 사용자의 소유가 아니라면 InvalidMonthlyGoalException을 발생시킨다.")
	void throwInvalidMonthlyGoalExceptionIfMonthlyGoalIsNotOwnedByMember() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal expected = mock(MonthlyGoal.class);
		given(monthlyGoalRepository.findById(eq(monthlyGoalId))).willReturn(Optional.of(expected));
		willReturn(false).given(expected).isOwnedBy(eq(memberId));

		assertThatThrownBy(() -> monthlyGoalQueryService.searchById(monthlyGoalId, memberId))
				.isInstanceOf(InvalidMonthlyGoalException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_ACCESS_DENIED.getDescription());
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 월 목표들을 조회한다.")
	void searchAllByDateTest() {
		Long memberId = 1L;
		Integer year = 2024;
		Integer month = 4;
		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) ->
										MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
												LocalDate.of(2024, 4, 1),
												LocalDate.of(2024, 4, 30),
												member))
						.toList();
		given(monthlyGoalRepository.findMonthlyGoalInMonth(eq(year), eq(month), eq(memberId)))
				.willReturn(monthlyGoals);

		List<MonthlyGoal> found = monthlyGoalQueryService.searchAllByDate(year, month, memberId);

		assertAll(
				() -> {
					assertThat(found).hasSize(monthlyGoals.size());
					found.forEach(mg -> assertThat(mg.getStartDate()).hasYear(year));
					found.forEach(mg -> assertThat(mg.getStartDate()).hasMonth(Month.of(month)));
				});
	}

	@Test
	@DisplayName("id에 해당하는 월 목표를 조회한다.")
	void searchByIdOrElseNull() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal expected = spy(MonthlyGoalFixture.DEFAULT.getWithMember(member));
		given(monthlyGoalRepository.findById(eq(monthlyGoalId))).willReturn(Optional.of(expected));
		willReturn(true).given(expected).isOwnedBy(eq(memberId));

		Optional<MonthlyGoal> found =
				monthlyGoalQueryService.searchOptionalById(monthlyGoalId, memberId);
		assertThat(found).isNotEmpty();
		assertThat(found.get()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	@DisplayName("id에 해당하는 월 목표가 존재하지 않을 경우 null을 반환한다.")
	void searchByIdOrElseNullReturnsNull() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		given(monthlyGoalRepository.findById(eq(monthlyGoalId))).willReturn(Optional.empty());

		Optional<MonthlyGoal> found =
				monthlyGoalQueryService.searchOptionalById(monthlyGoalId, memberId);
		assertThat(found).isEmpty();
	}

	@Test
	@DisplayName("월 목표가 null이 아니고, 해당 사용자의 소유가 아니라면 InvalidMonthlyGoalException을 발생시킨다.")
	void throwInvalidMonthlyGoalExceptionIfMonthlyGoalIsNotNullAndNotOwnedByMember() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal expected = mock(MonthlyGoal.class);
		given(monthlyGoalRepository.findById(eq(monthlyGoalId))).willReturn(Optional.of(expected));
		willReturn(false).given(expected).isOwnedBy(eq(memberId));

		assertThatThrownBy(
						() -> monthlyGoalQueryService.searchOptionalById(monthlyGoalId, memberId))
				.isInstanceOf(InvalidMonthlyGoalException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_ACCESS_DENIED.getDescription());
	}
}
