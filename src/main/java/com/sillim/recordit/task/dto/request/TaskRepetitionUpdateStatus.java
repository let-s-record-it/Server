package com.sillim.recordit.task.dto.request;

/**
 * NONE: 할 일 반복 변동 없음
 * CREATED: 할 일 반복 생성됨
 * DELETED: 할 일 반복 삭제됨
 * MODIFIED: 할 일 반복 수정됨
 */
public enum TaskRepetitionUpdateStatus {
	NONE,
	CREATED,
	DELETED,
	MODIFIED,
}
