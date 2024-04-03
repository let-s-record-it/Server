package com.sillim.recordit.goal.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyGoalUpdateServiceTest {

	@Mock MonthlyGoalJpaRepository monthlyGoalJpaRepository;

	@InjectMocks MonthlyGoalUpdateService monthlyGoalUpdateService;

	@Test
	@DisplayName("월 목표 추가 테스트")
	void addTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest("취뽀하기!", "취업할 때까지 숨 참는다.", 2024, 4, "#83c8ef");

		monthlyGoalUpdateService.add(request, 1L);

		// TODO: Member 조회 행위 검증
		then(monthlyGoalJpaRepository).should(times(1)).save(any(MonthlyGoal.class));
	}
}
