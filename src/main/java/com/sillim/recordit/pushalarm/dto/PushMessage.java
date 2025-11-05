package com.sillim.recordit.pushalarm.dto;

import com.sillim.recordit.schedule.domain.Schedule;
import java.util.List;

public record PushMessage<T>(Long id, AlarmType type, T content) {

	public static PushMessage<InviteMessage> fromInvite(
			Long activeId, String calendarTitle, String inviterPersonalId) {
		return new PushMessage<>(
				null,
				AlarmType.INVITE,
				new InviteMessage(
						activeId,
						inviterPersonalId + "님의 " + calendarTitle + "캘린더 초대",
						"참가하시겠습니까?"));
	}

	public static PushMessage<FollowMessage> fromFollowing(
			Long activeId, String followerPersonalId) {
		return new PushMessage<>(
				null,
				AlarmType.FOLLOWING,
				new FollowMessage(activeId, followerPersonalId + "님이 팔로우했습니다.", "맞팔로우하시겠습니까?"));
	}

	public static PushMessage<ScheduleAlarmMessage> fromScheduleAlarm(
			Long activeId, String scheduleTitle, String scheduledTime) {
		return new PushMessage<>(
				null,
				AlarmType.SCHEDULE_ALARM,
				new ScheduleAlarmMessage(activeId, scheduleTitle, scheduledTime));
	}

	public static PushMessage<FeedLikeMessage> fromFeedLike(
			Long feedLikeId, String likerPersonalId, String feedTitle) {
		return new PushMessage<>(
				null,
				AlarmType.FEED_LIKE,
				new FeedLikeMessage(
						feedLikeId, likerPersonalId + "님이 " + feedTitle + " 피드에 좋아요를 눌렀습니다."));
	}

	public static PushMessage<List<ScheduleAddMessage>> fromAddSchedules(List<Schedule> schedules) {
		return new PushMessage<>(
				null,
				AlarmType.SCHEDULE_ADD,
				schedules.stream().map(ScheduleAddMessage::from).toList());
	}

	public static PushMessage<List<ScheduleDeleteMessage>> fromDeleteSchedules(
			List<Schedule> schedules) {
		return new PushMessage<>(
				null,
				AlarmType.SCHEDULE_DELETE,
				schedules.stream().map(ScheduleDeleteMessage::from).toList());
	}
}
