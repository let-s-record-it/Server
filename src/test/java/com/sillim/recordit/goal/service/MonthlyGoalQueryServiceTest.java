package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyGoalQueryServiceTest {

	@Mock MonthlyGoalJpaRepository monthlyGoalJpaRepository;
	@InjectMocks MonthlyGoalQueryService monthlyGoalQueryService;
	Member member;

	@Test
	@DisplayName("월 목표 조회 service 테스트")
	void searchTest() {

		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		given(monthlyGoalJpaRepository.findById(anyLong())).willReturn(Optional.of(monthlyGoal));

		monthlyGoalQueryService.search(anyLong());

		then(monthlyGoalJpaRepository).should(times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("월 목표 조회 service 테스트 예외 - MonthlyGoal Not Found")
	void searchTestMonthlyGoalNotFound() {

		given(monthlyGoalJpaRepository.findById(anyLong()))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		assertThatThrownBy(() -> monthlyGoalQueryService.search(anyLong()))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MONTHLY_GOAL_NOT_FOUND.getDescription());
	}
}
