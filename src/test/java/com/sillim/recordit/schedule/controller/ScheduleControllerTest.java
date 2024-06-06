package com.sillim.recordit.schedule.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.fixture.RepetitionPatternFixture;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import com.sillim.recordit.schedule.service.RepetitionPatternService;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import com.sillim.recordit.schedule.service.ScheduleQueryService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest extends RestDocsTest {

	@MockBean ScheduleCommandService scheduleCommandService;
	@MockBean ScheduleQueryService scheduleQueryService;
	@MockBean RepetitionPatternService repetitionPatternService;

	Member member;
	Calendar calendar;

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
	}

	@Test
	@DisplayName("일정을 생성한다.")
	void addSchedule() throws Exception {
		long calendarId = 1L;
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						LocalDateTime.of(2024, 1, 1, 0, 0));
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0));
		given(scheduleCommandService.addSchedules(scheduleAddRequest, calendarId))
				.willReturn(List.of(schedule));

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/calendars/{calendarId}/schedules", calendarId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(scheduleAddRequest)));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("add-schedule", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("상세 일정을 조회한다.")
	void scheduleDetails() throws Exception {
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.build();
		given(scheduleQueryService.searchSchedule(anyLong())).willReturn(schedule);
		long calendarId = 1L;
		long scheduleId = 1L;

		ResultActions perform =
				mockMvc.perform(
						get(
								"/api/v1/calendars/{calendarId}/schedules/{scheduleId}",
								calendarId,
								scheduleId));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("schedule-details", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("반복이 있는 상세 일정을 조회한다.")
	void repeatedScheduleDetails() throws Exception {
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.colorHex("aaffbb")
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.build();
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.WEEKLY.getRepetitionPattern(scheduleGroup);
		given(repetitionPatternService.searchByScheduleGroupId(any()))
				.willReturn(repetitionPattern);
		given(scheduleQueryService.searchSchedule(anyLong())).willReturn(schedule);
		long calendarId = 1L;
		long scheduleId = 1L;

		ResultActions perform =
				mockMvc.perform(
						get(
								"/api/v1/calendars/{calendarId}/schedules/{scheduleId}",
								calendarId,
								scheduleId));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"repeated-schedule-details",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("특정 달의 일정을 조회한다.")
	void scheduleListInMonth() throws Exception {
		long calendarId = 1L;
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar);
		given(scheduleQueryService.searchSchedulesInMonth(calendarId, 2024, 1))
				.willReturn(List.of(schedule));

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/schedules/month", calendarId)
								.contentType(MediaType.APPLICATION_JSON)
								.queryParam("year", "2024")
								.queryParam("month", "1"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"schedule-list-in-month",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("특정 일의 일정을 조회한다.")
	void scheduleListInDay() throws Exception {
		long calendarId = 1L;
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		RepetitionPattern repetitionPattern =
				RepetitionPattern.createDaily(
						1,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						scheduleGroup);
		scheduleGroup.setRepetitionPattern(repetitionPattern);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0));
		given(scheduleQueryService.searchSchedulesInDay(calendarId, LocalDate.of(2024, 1, 15)))
				.willReturn(List.of(schedule));

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/schedules/day", calendarId)
								.contentType(MediaType.APPLICATION_JSON)
								.queryParam("date", "2024-01-15"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"schedule-list-in-day",
								getDocumentRequest(),
								getDocumentResponse()));
	}
}
