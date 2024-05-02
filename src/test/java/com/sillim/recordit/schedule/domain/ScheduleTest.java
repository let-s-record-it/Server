package com.sillim.recordit.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.vo.AlarmTime;
import com.sillim.recordit.schedule.domain.vo.Location;
import com.sillim.recordit.schedule.domain.vo.ScheduleColorHex;
import com.sillim.recordit.schedule.domain.vo.ScheduleDescription;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.domain.vo.ScheduleTitle;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleTest {

	Member member;
	Calendar calendar;
	ScheduleGroup scheduleGroup;

	@BeforeEach
	void initObjects() {
		member =
				Member.builder()
						.auth(new Auth("1234567", OAuthProvider.KAKAO))
						.name("name")
						.job("job")
						.deleted(false)
						.memberRole(List.of(MemberRole.ROLE_USER))
						.build();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
		scheduleGroup =
				ScheduleGroup.builder().isRepeated(false).member(member).calendar(calendar).build();
	}

	@Test
	@DisplayName("스케줄을 생성할 수 있다.")
	void createSchedule() {
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup);
		ScheduleFixture fixture = ScheduleFixture.DEFAULT;

		assertAll(
				() -> {
					assertThat(schedule.getTitle())
							.isEqualTo(new ScheduleTitle(fixture.getTitle()));
					assertThat(schedule.getDescription())
							.isEqualTo(new ScheduleDescription(fixture.getDescription()));
					assertThat(schedule.getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											fixture.getStartDatetime(), fixture.getEndDatetime()));
					assertThat(schedule.getColorHex())
							.isEqualTo(new ScheduleColorHex(fixture.getColorHex()));
					assertThat(schedule.getPlace()).isEqualTo(fixture.getPlace());
					assertThat(schedule.getSetLocation()).isEqualTo(fixture.getSetLocation());
					assertThat(schedule.getLocation())
							.isEqualTo(new Location(fixture.getLatitude(), fixture.getLongitude()));
					assertThat(schedule.getSetAlarm()).isEqualTo(fixture.getSetAlarm());
					assertThat(schedule.getAlarmTime())
							.isEqualTo(AlarmTime.create(fixture.getAlarmTime()));
				});
	}

	@Test
	@DisplayName("위치 설정 여부가 false이면 위치 값이 null이 저장된다.")
	void locationIsNullWhenSetLocationIsFalse() {
		Schedule schedule = ScheduleFixture.NOT_SET_LOCATION.getSchedule(scheduleGroup);

		assertThat(schedule.getLocation()).isNull();
	}

	@Test
	@DisplayName("알람 설정 여부가 false이면 알람 값이 null이 저장된다.")
	void alarmTimeIsNullWhenSetAlarmIsFalse() {
		Schedule schedule = ScheduleFixture.NOT_SET_ALARM.getSchedule(scheduleGroup);

		assertThat(schedule.getAlarmTime()).isNull();
	}
}
