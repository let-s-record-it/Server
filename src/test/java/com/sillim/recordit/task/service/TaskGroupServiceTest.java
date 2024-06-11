package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
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
	@Mock MonthlyGoalRepository monthlyGoalRepository;

	// TODO: WeeklyGoal 기능 구현 후 테스트 추가 필요

	@Test
	@DisplayName("연관 목표가 없는 할 일 그룹을 추가할 수 있다.")
	void addTaskGroupTest() {
		Long memberId = 1L;
		TaskGroup expected = new TaskGroup(false, null, null);
		given(monthlyGoalRepository.findById(any())).willReturn(Optional.empty());
		given(taskGroupRepository.save(any(TaskGroup.class))).willReturn(expected);

		TaskGroup saved = taskGroupService.addTaskGroup(false, null, null, memberId);

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "createdAt", "modifiedAt")
				.isEqualTo(expected);
		then(monthlyGoalRepository).should(times(1)).findById(any());
		then(taskGroupRepository).should(times(1)).save(any(TaskGroup.class));
	}
}
