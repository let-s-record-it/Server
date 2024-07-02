package com.sillim.recordit.task.domain.repetition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskWeeklyRepetitionPatternTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(false, null, null);
	}

	@Test
	@DisplayName("주간 반복 패턴을 생성할 수 있다.")
	void createWeeklyRepeatingPatternWithPeriod1() {
		TaskRepetitionPattern repetitionPattern =
				TaskWeeklyRepetitionPattern.createWeekly(
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 1, 31),
						Integer.parseInt("1110100", 2),
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(TaskRepetitionType.WEEKLY);
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1인 경우")
	void createWeeklyRepeatingPatternScheduleStreamWithPeriod1() {
		// 1월 1일 ~ 1월 31일까지, 수/금/토/일 반복
		TaskRepetitionPattern repetitionPattern =
				TaskWeeklyRepetitionPattern.createWeekly(
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 1, 31),
						Integer.parseInt("1110100", 2),
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.WEEKLY);
					assertThat(repeating).hasSize(17);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 3));
					assertThat(
									repetitionPattern
											.getRepetitionStartDate()
											.plus(repeating.get(repeating.size() - 1)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2인 경우")
	void createWeeklyRepeatingPatternScheduleStreamWithPeriod2() {
		// 1월 1일 ~ 1월 31일까지, 수/금/토/일 반복
		TaskRepetitionPattern repetitionPattern =
				TaskWeeklyRepetitionPattern.createWeekly(
						2,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 1, 31),
						Integer.parseInt("1110100", 2),
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.WEEKLY);
					assertThat(repeating).hasSize(9);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 3));
					assertThat(
									repetitionPattern
											.getRepetitionStartDate()
											.plus(repeating.get(repeating.size() - 1)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
				});
	}

	@Test
	@DisplayName("Weekday값을 통해 반복 설정된 비트를 찾을 수 있다.")
	void findValidBitByWeekday() {
		// 1월 1일 ~ 1월 31일까지, 수/금/토/일 반복
		TaskRepetitionPattern repetitionPattern =
				TaskWeeklyRepetitionPattern.createWeekly(
						2,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 1, 31),
						Integer.parseInt("1110100", 2),
						taskGroup);

		assertAll(
				() -> {
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.MONDAY)).isFalse();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.TUESDAY)).isFalse();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.WEDNESDAY)).isTrue();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.THURSDAY)).isFalse();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.FRIDAY)).isTrue();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.SATURDAY)).isTrue();
					assertThat(repetitionPattern.isValidWeekday(DayOfWeek.SUNDAY)).isTrue();
				});
	}
}
