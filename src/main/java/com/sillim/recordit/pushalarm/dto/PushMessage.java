package com.sillim.recordit.pushalarm.dto;

import com.sillim.recordit.pushalarm.domain.AlarmLog;

public record PushMessage(Long id, AlarmType type, Long activeId, String title, String body) {

	public static PushMessage fromInvite(
			Long activeId, String calendarTitle, String inviterPersonalId) {
		return new PushMessage(
				null,
				AlarmType.INVITE,
				activeId,
				inviterPersonalId + "님의 " + calendarTitle + "캘린더 초대",
				"참가하시겠습니까?");
	}

	public static PushMessage fromFollowing(Long activeId, String followerPersonalId) {
		return new PushMessage(
				null,
				AlarmType.FOLLOWING,
				activeId,
				followerPersonalId + "님이 팔로우했습니다.",
				"맞팔로우하시겠습니까?");
	}

	public static PushMessage fromSchedule(
			Long activeId, String scheduleTitle, String scheduledTime) {
		return new PushMessage(null, AlarmType.SCHEDULE, activeId, scheduleTitle, scheduledTime);
	}

	public static PushMessage fromAlarmLog(AlarmLog alarmLog) {
		return new PushMessage(
				alarmLog.getId(),
				alarmLog.getAlarmType(),
				alarmLog.getActiveId(),
				alarmLog.getTitle(),
				alarmLog.getBody());
	}
}
