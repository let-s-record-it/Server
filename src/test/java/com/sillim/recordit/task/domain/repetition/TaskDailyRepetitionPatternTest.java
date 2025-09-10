package com.sillim.recordit.task.domain.repetition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskDailyRepetitionPatternTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(null, null);
	}

	@Test
	@DisplayName("일간 반복 패턴을 생성할 수 있다.")
	void createDailyRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskDailyRepetitionPattern.createDaily(
						1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), taskGroup);
		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(TaskRepetitionType.DAILY);
	}

	@Test
	@DisplayName("일간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1일인 경우")
	void createDailyRepeatingPatternScheduleStreamWithPeriod1() {
		// 1월 1일 ~ 1월 31일까지 매일 반복
		TaskRepetitionPattern repetitionPattern =
				TaskDailyRepetitionPattern.createDaily(
						1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.DAILY);
					assertThat(repeating).hasSize(31);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 1));
					assertThat(
									repetitionPattern
											.getRepetitionStartDate()
											.plus(repeating.get(repeating.size() - 1)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
				});
	}

	@Test
	@DisplayName("일간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2일인 경우")
	void createDailyRepeatingPatternScheduleStreamWithPeriod2() {
		// 1월 1일 ~ 1월 31일까지 매일 반복
		TaskRepetitionPattern repetitionPattern =
				TaskDailyRepetitionPattern.createDaily(
						2, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.DAILY);
					assertThat(repeating).hasSize(16);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 1));
					assertThat(
									repetitionPattern
											.getRepetitionStartDate()
											.plus(repeating.get(repeating.size() - 1)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
				});
	}
}
