package com.sillim.recordit.task.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskDailyRepetitionPattern;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
import com.sillim.recordit.task.repository.TaskRepetitionPatternRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskRepetitionPatternServiceTest {

	@InjectMocks TaskRepetitionPatternService repetitionPatternService;
	@Mock TaskRepetitionPatternRepository repetitionPatternRepository;

	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(true, null, null);
	}

	@Test
	@DisplayName("할 일 반복 패턴을 추가할 수 있다.")
	void addRepetitionPatternTest() {
		TaskRepetitionUpdateRequest request =
				new TaskRepetitionUpdateRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);

		TaskRepetitionPattern repetitionPattern = TaskRepetitionPatternFixture.DAILY.get(taskGroup);
		given(repetitionPatternRepository.save(any(TaskRepetitionPattern.class)))
				.willReturn(repetitionPattern);

		repetitionPatternService.addRepetitionPattern(request, taskGroup);

		then(repetitionPatternRepository)
				.should(times(1))
				.save(any(TaskDailyRepetitionPattern.class));
	}

	@Test
	@DisplayName("할 일 반복 패턴을 수정할 경우, 기존의 것을 삭제하고 새로 생성한다.")
	void modifyRepetitionPattern() {
		Long taskGroupId = 1L;
		taskGroup = spy(taskGroup);
		given(taskGroup.getId()).willReturn(taskGroupId);
		TaskRepetitionUpdateRequest request =
				new TaskRepetitionUpdateRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);
		TaskRepetitionPattern repetitionPattern = TaskRepetitionPatternFixture.DAILY.get(taskGroup);
		given(repetitionPatternRepository.save(any(TaskRepetitionPattern.class)))
				.willReturn(repetitionPattern);

		repetitionPatternService.modifyRepetitionPattern(request, taskGroup);

		then(repetitionPatternRepository).should(times(1)).deleteByTaskGroupId(eq(taskGroupId));
		then(repetitionPatternRepository)
				.should(times(1))
				.save(any(TaskDailyRepetitionPattern.class));
	}
}
