package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlarmTimeTest {

	@Test
	@DisplayName("AlarmTime을 생성할 수 있다.")
	void createAlarmTime() {
		AlarmTime alarmTime = AlarmTime.create(LocalDateTime.of(2024, 1, 1, 0, 0));

		assertAll(() -> {
			assertThat(alarmTime).isEqualTo(AlarmTime.create(LocalDateTime.of(2024, 1, 1, 0, 0)));
			assertThat(alarmTime.getAlarmTime()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
		});
	}
}
