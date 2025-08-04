package com.sillim.recordit.global.lock;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisLockUtil {

	private static LockManager lockManager;

	public RedisLockUtil(LockManager lockManager) {
		RedisLockUtil.lockManager = lockManager;
	}

	public static <T> T acquireAndRunLock(String key, Supplier<T> block) {
		if (key.isBlank()) {
			log.error("[Redis] Key가 Blank 상태입니다.");
			return block.get();
		}

		boolean acquired = acquireLock(key);

		if (acquired) {
			return proceedWithLock(key, block);
		}
		throw new ApplicationException(ErrorCode.FAILED_TO_ACQUIRE_REDIS_LOCK);
	}

	private static boolean acquireLock(String key) {
		try {
			return lockManager.lock(key);
		} catch (Exception e) {
			log.error("[Redis] Lock 획득에 실패했습니다. key: {} {}", key, e.getMessage());
			return false;
		}
	}

	private static <T> T proceedWithLock(String key, Supplier<T> block) {
		try {
			return block.get();
		} catch (Exception e) {
			throw e;
		} finally {
			releaseLock(key);
		}
	}

	private static boolean releaseLock(String key) {
		try {
			return lockManager.unlock(key);
		} catch (Exception e) {
			log.error("[Redis] Lock 해제에 실패했습니다. key: {} {}", key, e.getMessage());
			return false;
		}
	}
}
