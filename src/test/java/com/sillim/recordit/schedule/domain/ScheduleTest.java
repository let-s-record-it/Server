package com.sillim.recordit.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.vo.ScheduleDuration;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleTest {

	Member member;
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
		scheduleGroup = new ScheduleGroup(false);
	}

	@Test
	@DisplayName("스케줄을 생성할 수 있다.")
	void createSchedule() {
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup);
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
		Schedule schedule = ScheduleFixture.NOT_SET_LOCATION.getSchedule(scheduleGroup);

		assertThat(schedule.getLocation()).isNull();
	}

	@Test
	@DisplayName("알람 설정 여부가 false이면 알람 값에 null이 저장된다.")
	void alarmTimeIsNullWhenSetAlarmIsFalse() {
		Schedule schedule = ScheduleFixture.NOT_SET_ALARM.getSchedule(scheduleGroup);

		assertThat(schedule.getAlarmTime()).isNull();
	}
}
