package com.sillim.recordit.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleTest {

	Member member;
	Calendar calendar;
	ScheduleGroup scheduleGroup;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
		scheduleGroup = new ScheduleGroup(false);
	}

	@Test
	@DisplayName("스케줄을 생성할 수 있다.")
	void createSchedule() {
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		ScheduleFixture fixture = ScheduleFixture.DEFAULT;

		assertAll(
				() -> {
					assertThat(schedule.getTitle()).isEqualTo(fixture.getTitle());
					assertThat(schedule.getDescription()).isEqualTo(fixture.getDescription());
					assertThat(schedule.getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											fixture.getStartDatetime(), fixture.getEndDatetime()));
					assertThat(schedule.getColorHex()).isEqualTo(fixture.getColorHex());
					assertThat(schedule.getPlace()).isEqualTo(fixture.getPlace());
					assertThat(schedule.getSetLocation()).isEqualTo(fixture.getSetLocation());
					assertThat(schedule.getLatitude()).isEqualTo(fixture.getLatitude());
					assertThat(schedule.getLongitude()).isEqualTo(fixture.getLongitude());
					assertThat(schedule.getSetAlarm()).isEqualTo(fixture.getSetAlarm());
					assertThat(schedule.getAlarmTime()).isEqualTo(fixture.getAlarmTime());
				});
	}

	@Test
	@DisplayName("위치 설정 여부가 false이면 위치 값에 null이 저장된다.")
	void locationIsNullWhenSetLocationIsFalse() {
		Schedule schedule = ScheduleFixture.NOT_SET_LOCATION.getSchedule(scheduleGroup, calendar);

		assertThat(schedule.getLocation()).isNull();
	}

	@Test
	@DisplayName("알람 설정 여부가 false이면 알람 값에 null이 저장된다.")
	void alarmTimeIsNullWhenSetAlarmIsFalse() {
		Schedule schedule = ScheduleFixture.NOT_SET_ALARM.getSchedule(scheduleGroup, calendar);

		assertThat(schedule.getAlarmTime()).isNull();
	}
}
