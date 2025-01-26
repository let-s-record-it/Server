package com.sillim.recordit.schedule.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

@Slf4j
public class ScheduleAlarmJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String title = jobDataMap.getString("title");
		String body = jobDataMap.getString("body");
		List<String> fcmTokens = (List<String>) jobDataMap.get("fcmTokens");
		String scheduleId = String.valueOf(jobDataMap.getLong("scheduleId"));

		fcmTokens.forEach(
				fcmToken -> {
					try {
						Message message =
								Message.builder()
										.setNotification(
												Notification.builder()
														.setTitle(title)
														.setBody(body)
														.build())
										.putData("scheduleId", scheduleId)
										.setToken(fcmToken)
										.build();

						String response = FirebaseMessaging.getInstance().send(message);
						log.info("send push message response: {}", response);
					} catch (FirebaseMessagingException e) {
						throw new RuntimeException(e);
					}
				});
	}
}
