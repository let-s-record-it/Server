package com.sillim.recordit.goal.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyGoalUpdateServiceTest {

	@Mock MonthlyGoalQueryService monthlyGoalQueryService;
	@Mock MonthlyGoalJpaRepository monthlyGoalJpaRepository;
	@InjectMocks MonthlyGoalUpdateService monthlyGoalUpdateService;
	Member member = new Member();

	@Test
	@DisplayName("새로운 월 목표를 추가한다.")
	void addTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest("취뽀하기!", "취업할 때까지 숨 참는다.", 2024, 4, "#83c8ef");

		monthlyGoalUpdateService.add(request, anyLong());

		// TODO: Member 조회 행위 검증
		then(monthlyGoalJpaRepository).should(times(1)).save(any(MonthlyGoal.class));
	}

	@Test
	@DisplayName("기존의 월 목표를 수정한다.")
	void modifyTest() {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest("(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다.", 2024, 5, "#123456");
		MonthlyGoal monthlyGoal = MonthlyGoalFixture.DEFAULT.getWithMember(member);
		given(monthlyGoalQueryService.search(anyLong())).willReturn(monthlyGoal);

		monthlyGoalUpdateService.modify(request, anyLong());

		then(monthlyGoalQueryService).should(times(1)).search(anyLong());
	}
}