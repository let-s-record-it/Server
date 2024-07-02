package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.repository.TaskGroupRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskGroupServiceTest {

	@InjectMocks TaskGroupService taskGroupService;
	@Mock TaskGroupRepository taskGroupRepository;
	@Mock MonthlyGoalQueryService monthlyGoalQueryService;

	// TODO: WeeklyGoal 기능 구현 후 테스트 추가 필요

	@Test
	@DisplayName("연관 목표가 없는 할 일 그룹을 추가할 수 있다.")
	void addTaskGroupTest() {
		Long memberId = 1L;
		TaskGroup expected = new TaskGroup(false, null, null);
		given(monthlyGoalQueryService.searchOptionalById(any(), eq(memberId)))
				.willReturn(Optional.empty());
		given(taskGroupRepository.save(any(TaskGroup.class))).willReturn(expected);

		TaskGroup saved = taskGroupService.addTaskGroup(false, null, null, memberId);

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "createdAt", "modifiedAt")
				.isEqualTo(expected);
		then(taskGroupRepository).should(times(1)).save(any(TaskGroup.class));
	}

	@Test
	@DisplayName("월 연관 목표가 있는 할 일 그룹을 추가할 수 있다.")
	void addTaskGroupTest_WithRelatedGoals() {
		Long memberId = 1L;
		Long relatedMonthlyGoalId = 1L;
		MonthlyGoal monthlyGoal =
				MonthlyGoalFixture.DEFAULT.getWithMember(MemberFixture.DEFAULT.getMember());
		TaskGroup expected = new TaskGroup(false, monthlyGoal, null);

		given(monthlyGoalQueryService.searchOptionalById(eq(relatedMonthlyGoalId), eq(memberId)))
				.willReturn(Optional.of(monthlyGoal));
		given(taskGroupRepository.save(any(TaskGroup.class))).willReturn(expected);

		TaskGroup saved =
				taskGroupService.addTaskGroup(false, relatedMonthlyGoalId, null, memberId);

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "weeklyGoal", "createdAt", "modifiedAt")
				.isEqualTo(expected);
		then(taskGroupRepository).should(times(1)).save(any(TaskGroup.class));
	}
}
