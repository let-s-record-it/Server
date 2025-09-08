package com.sillim.recordit.pushalarm.service;

import com.sillim.recordit.member.service.MemberDeviceService;
import com.sillim.recordit.schedule.service.ScheduleAlarmJob;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PushAlarmService {

	private final MemberDeviceService memberDeviceService;
	private final Scheduler scheduler;

	/**
	 *
	 * @param jobGroupName
	 *            그룹 이름 형식 : DOMAIN/memberId/groupId ex) SCHEDULE/1/1 ->
	 *            SCHEDULE/memberId/scheduleGroupId
	 * @author 서원호
	 */
	public void reservePushAlarmJobs(Long memberId, String jobGroupName, String title, String body,
			Map<String, Object> payload, List<LocalDateTime> dateTimes) throws SchedulerException {
		List<String> fcmTokens = memberDeviceService.searchFcmTokensByMemberId(memberId);

		for (LocalDateTime alarm : dateTimes) {
			JobDetail job = JobBuilder.newJob(ScheduleAlarmJob.class).withIdentity(alarm.toString(), jobGroupName)
					.build();
			job.getJobDataMap().put("title", title);
			job.getJobDataMap().put("body", body);
			job.getJobDataMap().put("fcmTokens", fcmTokens);
			payload.forEach((key, value) -> {
				log.info("{} {}", key, value);
				job.getJobDataMap().put(key, value);
			});
			Date date = Date.from(alarm.atZone(ZoneId.systemDefault()).toInstant());
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(alarm.toString(), memberId.toString())
					.startAt(date).build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			log.info("JOB SCHEDULED: {}, TIME: {}", job.getKey().getGroup(), date);
		}
	}

	public void deletePushAlarmJobs(String jobGroupName) throws SchedulerException {
		Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(jobGroupName));
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				scheduler.unscheduleJob(trigger.getKey());
			}

			scheduler.deleteJob(jobKey);
			log.info("JOB DELETED: {}", jobKey.getName());
		}
	}
}
