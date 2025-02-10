package com.sillim.recordit.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
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
	CalendarCategory category;
	ScheduleGroup scheduleGroup;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
		category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member, category);
		scheduleGroup = new ScheduleGroup(false);
	}

	@Test
	@DisplayName("스케줄을 생성할 수 있다.")
	void createSchedule() {
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(scheduleCategory, scheduleGroup, calendar);
		ScheduleFixture fixture = ScheduleFixture.DEFAULT;

		assertAll(
				() -> {
					assertThat(schedule.getTitle()).isEqualTo(fixture.getTitle());
					assertThat(schedule.getDescription()).isEqualTo(fixture.getDescription());
					assertThat(schedule.getScheduleDuration())
							.isEqualTo(
									ScheduleDuration.createNotAllDay(
											fixture.getStartDatetime(), fixture.getEndDatetime()));
					assertThat(schedule.getColorHex()).isEqualTo(scheduleCategory.getColorHex());
					assertThat(schedule.getPlace()).isEqualTo(fixture.getPlace());
					assertThat(schedule.isSetLocation()).isEqualTo(fixture.getSetLocation());
					assertThat(schedule.getLatitude()).isEqualTo(fixture.getLatitude());
					assertThat(schedule.getLongitude()).isEqualTo(fixture.getLongitude());
					assertThat(schedule.isSetAlarm()).isEqualTo(fixture.getSetAlarm());
				});
	}

	@Test
	@DisplayName("위치 설정 여부가 false이면 위치 값에 null이 저장된다.")
	void locationIsNullWhenSetLocationIsFalse() {
		ScheduleCategory scheduleCategory =
				ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
		Schedule schedule =
				ScheduleFixture.NOT_SET_LOCATION.getSchedule(
						scheduleCategory, scheduleGroup, calendar);

		assertThat(schedule.getLocation()).isNull();
	}
}
