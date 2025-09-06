package com.sillim.recordit.schedule.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleModifyRequest;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.dto.response.RepetitionPatternResponse;
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

	long memberId = 1L;
	Member member;
	CalendarCategory category;
	Calendar calendar;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
		category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
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
						"서울역",
						true,
						36.0,
						127.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				ScheduleFixture.DEFAULT.getSchedule(
						category,
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
				.andDo(document("schedule-modify", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("상세 일정을 조회한다.")
	void scheduleDetails() throws Exception {
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		Schedule schedule =
				Schedule.builder()
						.title("title")
						.description("description")
						.isAllDay(false)
						.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
						.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
						.setLocation(true)
						.place("서울역")
						.latitude(36.0)
						.longitude(127.0)
						.setAlarm(true)
						.category(category)
						.scheduleGroup(scheduleGroup)
						.calendar(calendar)
						.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
						.build();
		DayScheduleResponse dayScheduleResponse =
				DayScheduleResponse.of(
						schedule, false, List.of(LocalDateTime.of(2024, 1, 1, 0, 0)), null);
		given(scheduleQueryService.searchSchedule(anyLong(), any()))
				.willReturn(dayScheduleResponse);
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
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.WEEKLY.getRepetitionPattern(scheduleGroup);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		DayScheduleResponse dayScheduleResponse =
				DayScheduleResponse.of(
						Schedule.builder()
								.title("title")
								.description("description")
								.isAllDay(false)
								.startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
								.endDateTime(LocalDateTime.of(2024, 2, 1, 0, 0))
								.setLocation(true)
								.place("서울역")
								.latitude(36.0)
								.longitude(127.0)
								.setAlarm(true)
								.category(category)
								.scheduleGroup(scheduleGroup)
								.calendar(calendar)
								.scheduleAlarms(List.of(LocalDateTime.of(2024, 1, 1, 0, 0)))
								.build(),
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						RepetitionPatternResponse.from(repetitionPattern));
		given(repetitionPatternService.searchByScheduleGroupId(any()))
				.willReturn(repetitionPattern);
		given(scheduleQueryService.searchSchedule(anyLong(), any()))
				.willReturn(dayScheduleResponse);
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
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		Schedule schedule = ScheduleFixture.DEFAULT.getSchedule(category, scheduleGroup, calendar);
		MonthScheduleResponse monthScheduleResponse = MonthScheduleResponse.from(schedule);
		given(scheduleQueryService.searchSchedulesInMonth(calendarId, 2024, 1, 1L))
				.willReturn(List.of(monthScheduleResponse));

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
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		DayScheduleResponse dayScheduleResponse =
				DayScheduleResponse.of(
						ScheduleFixture.DEFAULT.getSchedule(
								category,
								scheduleGroup,
								calendar,
								LocalDateTime.of(2024, 1, 1, 0, 0),
								LocalDateTime.of(2024, 2, 1, 0, 0)),
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						RepetitionPatternResponse.from(
								RepetitionPattern.createDaily(
										1,
										LocalDateTime.of(2024, 1, 1, 0, 0),
										LocalDateTime.of(2024, 2, 1, 0, 0),
										scheduleGroup)));
		given(scheduleQueryService.searchSchedulesInDay(calendarId, LocalDate.of(2024, 1, 15), 1L))
				.willReturn(List.of(dayScheduleResponse));

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

	@Test
	@DisplayName("단일 일정을 수정한다.")
	void scheduleModify() throws Exception {
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"서울역",
						true,
						36.0,
						127.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/calendars/{calendarId}/schedules/{scheduleId}", 1L, 1L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(scheduleModifyRequest)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("schedule-modify", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("그룹 일정을 수정한다.")
	void schedulesModifyInGroup() throws Exception {
		ScheduleModifyRequest scheduleModifyRequest =
				new ScheduleModifyRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"서울역",
						true,
						36.0,
						127.0,
						true,
						1L,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)),
						1L);

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/calendars/{calendarId}/schedules/{scheduleId}/group", 1L, 1L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(scheduleModifyRequest)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"schedules-modify-in-group",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("일정을 삭제한다.")
	void scheduleRemove() throws Exception {
		ResultActions perform =
				mockMvc.perform(
						delete("/api/v1/calendars/{calendarId}/schedules/{scheduleId}", 1L, 1L)
								.contentType(MediaType.APPLICATION_JSON));

		verify(scheduleCommandService, times(1)).removeSchedule(eq(1L), any());
		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("schedule-remove", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("그룹 내의 모든 일정을 삭제한다.")
	void scheduleRemoveInGroup() throws Exception {
		ResultActions perform =
				mockMvc.perform(
						delete(
										"/api/v1/calendars/{calendarId}/schedules/{scheduleId}/group",
										1L,
										1L)
								.contentType(MediaType.APPLICATION_JSON));

		verify(scheduleCommandService, times(1)).removeGroupSchedules(eq(1L), any());
		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"schedule-remove-in-group",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("그룹 내 특정 시점 이후 모든 일정을 삭제한다.")
	void scheduleRemoveInGroupAfter() throws Exception {
		ResultActions perform =
				mockMvc.perform(
						delete(
										"/api/v1/calendars/{calendarId}/schedules/{scheduleId}/after",
										1L,
										1L)
								.contentType(MediaType.APPLICATION_JSON));

		verify(scheduleCommandService, times(1)).removeGroupSchedulesAfterCurrent(eq(1L), any());
		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"schedule-remove-in-group-after",
								getDocumentRequest(),
								getDocumentResponse()));
	}
}
