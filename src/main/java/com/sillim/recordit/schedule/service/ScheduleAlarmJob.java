package com.sillim.recordit.schedule.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class ScheduleAlarmJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		String title = context.getJobDetail().getJobDataMap().getString("title");
		String body = context.getJobDetail().getJobDataMap().getString("body");
		String pushAlarmToken = context.getJobDetail().getJobDataMap().getString("pushAlarmToken");
		try {
			Message message =
					Message.builder()
							.setNotification(
									Notification.builder().setTitle(title).setBody(body).build())
							.setToken(pushAlarmToken)
							.build();

			String response = FirebaseMessaging.getInstance().send(message);
			log.info("send push message response: {}", response);
		} catch (FirebaseMessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
