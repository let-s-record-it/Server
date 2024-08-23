package com.sillim.recordit.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeeklyGoalUpdateServiceTest {

	@Mock MemberQueryService memberQueryService;
	@Mock WeeklyGoalRepository weeklyGoalRepository;
	@InjectMocks WeeklyGoalUpdateService weeklyGoalUpdateService;
	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("새로운 월 목표를 추가한다.")
	void addTest() {
		Long memberId = 1L;
		Long monthlyGoalId = 2L;
		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef");
		WeeklyGoal saved = mock(WeeklyGoal.class);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(weeklyGoalRepository.save(any(WeeklyGoal.class))).willReturn(saved);
		given(saved.getId()).willReturn(monthlyGoalId);

		Long savedId = weeklyGoalUpdateService.addWeeklyGoal(request, memberId);

		assertThat(savedId).isEqualTo(monthlyGoalId);
		then(weeklyGoalRepository).should(times(1)).save(any(WeeklyGoal.class));
	}
}
