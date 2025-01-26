package com.sillim.recordit.pushalarm.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.service.MemberDeviceService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

@ExtendWith(MockitoExtension.class)
class PushAlarmServiceTest {

	@Mock MemberDeviceService memberDeviceService;
	@Mock Scheduler scheduler;
	@InjectMocks PushAlarmService pushAlarmService;

	@Test
	@DisplayName("일정 푸시 알람 Job을 등록할 수 있다.")
	void schedulePushAlarmJob() throws SchedulerException {
		long memberId = 1L;
		long scheduleGroupId = 1L;
		long scheduleId = 1L;
		String jobGroupName = memberId + "/" + scheduleGroupId + "/" + scheduleId;
		String title = "title";
		String body = "body";
		Member member =
				Member.createNoJobMember(
						new Auth("12345", OAuthProvider.KAKAO), "name", "https://image.url");
		List<LocalDateTime> alarmTimes = List.of(LocalDateTime.of(2024, 1, 1, 0, 0));
		given(memberDeviceService.searchFcmTokensByMemberId(eq(memberId)))
				.willReturn(List.of("token"));

		pushAlarmService.reservePushAlarmJobs(
				memberId, jobGroupName, title, body, Map.of("test", "test"), alarmTimes);

		assertAll(
				() -> {
					then(scheduler)
							.should(times(1))
							.scheduleJob(any(JobDetail.class), any(Trigger.class));
					then(scheduler).should(times(1)).start();
				});
	}

	@Test
	@DisplayName("일정 푸시 알림 Job을 삭제할 수 있다.")
	void deletePushAlarmJobs() throws SchedulerException {
		long memberId = 1L;
		long scheduleGroupId = 1L;
		long scheduleId = 1L;
		String jobGroupName = memberId + "/" + scheduleGroupId + "/" + scheduleId;
		JobKey jobKey = new JobKey("1/1/1");
		SimpleTrigger trigger =
				TriggerBuilder.newTrigger()
						.withIdentity("test", "1/1/1")
						.startNow()
						.withSchedule(SimpleScheduleBuilder.simpleSchedule())
						.build();

		Set<JobKey> jobKeys = Set.of(jobKey);
		given(scheduler.getJobKeys(any(GroupMatcher.class))).willReturn(jobKeys);
		when(scheduler.getTriggersOfJob(eq(jobKey)))
				.thenAnswer(unused -> Collections.singletonList(trigger));

		pushAlarmService.deletePushAlarmJobs(jobGroupName);

		assertAll(
				() -> {
					then(scheduler).should(times(1)).unscheduleJob(any(TriggerKey.class));
					then(scheduler).should(times(1)).deleteJob(any(JobKey.class));
				});
	}
}
