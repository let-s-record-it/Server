package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.time.LocalDate;
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

	@Mock MemberQueryService memberQueryService;
	@Mock MonthlyGoalJpaRepository monthlyGoalJpaRepository;
	@InjectMocks MonthlyGoalQueryService monthlyGoalQueryService;
	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("id를 기반으로 월 목표를 조회한다.")
	void searchTest() {

		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		given(monthlyGoalJpaRepository.findById(anyLong())).willReturn(Optional.of(monthlyGoal));

		monthlyGoalQueryService.search(anyLong());

		then(monthlyGoalJpaRepository).should(times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("id에 해당하는 월 목표가 존재하지 않을 경우 RecordNotFoundException을 발생시킨다.")
	void searchTestMonthlyGoalNotFound() {

		given(monthlyGoalJpaRepository.findById(anyLong()))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		assertThatThrownBy(() -> monthlyGoalQueryService.search(anyLong()))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 월 목표들을 조회한다.")
	void searchAllByDateTest() {

		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj((id) -> MonthlyGoalFixture.DEFAULT.getWithMember(member))
						.toList();
		given(memberQueryService.findByMemberId(anyLong())).willReturn(member);
		given(
						monthlyGoalJpaRepository.findByStartDateAndEndDateAndMember(
								any(LocalDate.class), any(LocalDate.class), any(Member.class)))
				.willReturn(monthlyGoals);

		monthlyGoalQueryService.searchAllByDate(
				LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30), 1L);

		then(memberQueryService).should(times(1)).findByMemberId(anyLong());
		then(monthlyGoalJpaRepository)
				.should(times(1))
				.findByStartDateAndEndDateAndMember(
						any(LocalDate.class), any(LocalDate.class), any(Member.class));
	}
}
