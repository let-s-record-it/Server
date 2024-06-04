package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
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
	@Mock MonthlyGoalRepository monthlyGoalRepository;
	@InjectMocks MonthlyGoalQueryService monthlyGoalQueryService;
	private Member member;

	@BeforeEach
	void beforeEach() {
		member = spy(MemberFixture.DEFAULT.getMember());
	}

	@Test
	@DisplayName("id를 기반으로 월 목표를 조회한다.")
	void searchTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		MonthlyGoal expected = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(monthlyGoalRepository.findByIdAndMember(eq(monthlyGoalId), any(Member.class)))
				.willReturn(Optional.of(expected));

		MonthlyGoal monthlyGoal = monthlyGoalQueryService.search(monthlyGoalId, memberId);
		then(memberQueryService).should(times(1)).findByMemberId(eq(memberId));
		then(monthlyGoalRepository)
				.should(times(1))
				.findByIdAndMember(eq(monthlyGoalId), any(Member.class));
	}

	@Test
	@DisplayName("id에 해당하는 월 목표가 존재하지 않을 경우 RecordNotFoundException을 발생시킨다.")
	void searchTestMonthlyGoalNotFound() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(monthlyGoalRepository.findByIdAndMember(eq(monthlyGoalId), any(Member.class)))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		assertThatThrownBy(() -> monthlyGoalQueryService.search(monthlyGoalId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_NOT_FOUND.getDescription());
		then(memberQueryService).should(times(1)).findByMemberId(eq(memberId));
		then(monthlyGoalRepository)
				.should(times(1))
				.findByIdAndMember(eq(monthlyGoalId), any(Member.class));
	}

	@Test
	@DisplayName("startDate와 endDate를 기반으로 해당 월 목표들을 조회한다.")
	void searchAllByDateTest() {
		Long memberId = 1L;
		Integer year = 2024;
		Integer month = 4;
		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj((id) -> MonthlyGoalFixture.DEFAULT.getWithMember(member))
						.toList();
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(monthlyGoalRepository.findMonthlyGoalInMonth(eq(year), eq(month), any(Member.class)))
				.willReturn(monthlyGoals);

		monthlyGoalQueryService.searchAllByDate(year, month, memberId);

		then(memberQueryService).should(times(1)).findByMemberId(eq(memberId));
		then(monthlyGoalRepository)
				.should(times(1))
				.findMonthlyGoalInMonth(eq(year), eq(month), any(Member.class));
	}
}
