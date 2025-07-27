package com.sillim.recordit.global.lock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class RedisLockUtilTest {

	@Test
	@DisplayName("key가 공백이면 lock을 획득하지 않고 로직 실행 결과를 반환한다.")
	void notAcquireLockAndReturnBusinessLogicResultIfKeyIsBlank() {
		String result = RedisLockUtil.acquireAndRunLock("", () -> "123");

		assertThat(result).isEqualTo("123");
	}

	@Test
	@DisplayName("lock을 획득하면 비즈니스 로직을 실행하고 그 결과를 반환한다.")
	void returnBusinessLogicResultIfAcquiredLock() {
		String result = RedisLockUtil.acquireAndRunLock("key", () -> "123");

		assertThat(result).isEqualTo("123");
	}
}
