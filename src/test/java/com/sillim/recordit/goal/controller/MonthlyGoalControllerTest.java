package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MonthlyGoalController.class)
public class MonthlyGoalControllerTest extends RestDocsTest {

	@MockBean MonthlyGoalUpdateService monthlyGoalUpdateService;
	@MockBean MonthlyGoalQueryService monthlyGoalQueryService;

	private Member member;
	private ScheduleCategory category;
	private CalendarCategory calendarCategory;
	private Calendar calendar;
	long memberId = 1L;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
		calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
	}

	@Test
	@DisplayName("새로운 월 목표를 추가한다.")
	void monthlyGoalAddTest() throws Exception {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						LocalDate.of(2024, 4, 1),
						LocalDate.of(2024, 4, 30),
						1L,
						1L);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/calendars/{calendarId}/monthly-goals", 1L)
								.headers(authorizationHeader())
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-add",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc())));
	}

	@Test
	@DisplayName("기존의 월 목표를 수정한다.")
	void monthlyGoalModifyTest() throws Exception {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"(수정)취뽀하기!",
						"(수정)취업할 때까지 숨 참는다.",
						LocalDate.of(2024, 5, 1),
						LocalDate.of(2024, 5, 31),
						1L,
						1L);

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/calendars/{calendarId}/monthly-goals/{monthlyGoalId}", 1L, 1L)
								.headers(authorizationHeader())
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-modify",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc())));
	}

	@Test
	@DisplayName("존재하지 않는 월 목표를 수정할 경우 NOT FOUND 응답을 반환한다.")
	void monthlyGoalModifyTestMonthlyGoalNotFound() throws Exception {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"(수정)취뽀하기!",
						"(수정)취업할 때까지 숨 참는다.",
						LocalDate.of(2024, 5, 1),
						LocalDate.of(2024, 5, 31),
						1L,
						1L);
		willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND))
				.given(monthlyGoalUpdateService)
				.modify(any(MonthlyGoalUpdateRequest.class), anyLong(), any());

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/calendars/{calendarId}/monthly-goals/{monthlyGoalId}", 1L, 1L)
								.headers(authorizationHeader())
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNotFound());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-modify-monthly-goal-not-found",
								getDocumentResponse()));
	}

	@Test
	@DisplayName("당월의 월 목표 목록을 조회한다.")
	void monthlyGoalListTest() throws Exception {
		List<MonthlyGoal> monthlyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) -> {
									MonthlyGoal goal =
											spy(
													MonthlyGoalFixture.DEFAULT.getWithMember(
															category, calendar));
									given(goal.getId()).willReturn(id);
									given(goal.isAchieved()).willReturn(id % 2 == 0);
									return goal;
								})
						.toList();

		given(monthlyGoalQueryService.searchAllByDate(anyInt(), anyInt(), anyLong()))
				.willReturn(monthlyGoals);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/monthly-goals", 1L)
								.headers(authorizationHeader())
								.queryParam("year", "2024")
								.queryParam("month", "4"));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$.[0].id").value(1))
				.andExpect(jsonPath("$.[0].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.[0].achieved").value(false))
				.andExpect(jsonPath("$.[1].id").value(2))
				.andExpect(jsonPath("$.[1].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.[1].achieved").value(true))
				.andExpect(jsonPath("$.[2].id").value(3))
				.andExpect(jsonPath("$.[2].title").value("취뽀하기!"))
				.andExpect(jsonPath("$.[2].achieved").value(false));

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-list",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 id")),
								queryParameters(
										parameterWithName("year").description("조회할 연도"),
										parameterWithName("month").description("조회할 월"))));
	}

	@Test
	@DisplayName("특정 id의 월 목표를 상세하게 조회한다.")
	void monthlyGoalDetailsTest() throws Exception {

		MonthlyGoal monthlyGoal = spy(MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar));
		given(monthlyGoal.getId()).willReturn(1L);

		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(anyLong()))
				.willReturn(monthlyGoal);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/monthly-goals/{id}", 1L, 1L)
								.headers(authorizationHeader()));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(monthlyGoal.getId()))
				.andExpect(jsonPath("$.title").value(monthlyGoal.getTitle()))
				.andExpect(jsonPath("$.startDate").value(monthlyGoal.getStartDate().toString()))
				.andExpect(jsonPath("$.endDate").value(monthlyGoal.getEndDate().toString()))
				.andExpect(jsonPath("$.description").value(monthlyGoal.getDescription()))
				.andExpect(jsonPath("$.colorHex").value(monthlyGoal.getColorHex()));

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-details",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("조회할 월 목표 activeId"),
										parameterWithName("calendarId")
												.description("캘린더 activeId"))));
	}

	@Test
	@DisplayName("존재하지 않는 월 목표를 상세하게 조회할 경우 NOT FOUND 응답을 반환한다.")
	void monthlyGoalDetailsNotFoundTest() throws Exception {

		given(monthlyGoalQueryService.searchByIdAndCheckAuthority(anyLong()))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/monthly-goals/{id}", 1L, 1L)
								.headers(authorizationHeader()));

		perform.andExpect(status().isNotFound());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-details-monthly-goal-not-found",
								getDocumentResponse()));
	}

	@Test
	@DisplayName("특정 id의 월 목표를 달성 상태로 변경한다.")
	void monthlyGoalChangeAchieveStatusTest() throws Exception {

		willDoNothing()
				.given(monthlyGoalUpdateService)
				.changeAchieveStatus(anyLong(), anyBoolean(), any());

		ResultActions perform =
				mockMvc.perform(
						patch("/api/v1/calendars/{calendarId}/monthly-goals/{id}/achieve", 1L, 1L)
								.headers(authorizationHeader())
								.queryParam("status", "true"));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-change-achieve-status",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("달성 상태를 변경할 월 목표 id"),
										parameterWithName("calendarId").description("캘린더 id")),
								queryParameters(
										parameterWithName("status")
												.description("달성 상태(false, true)"))));
	}

	@Test
	@DisplayName("존재하지 않는 월 목표의 달성 상태를 변경할 경우 NOT FOUND 응답을 반환한다.")
	void monthlyGoalChangeAchieveStatusNotFoundTest() throws Exception {

		willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND))
				.given(monthlyGoalUpdateService)
				.changeAchieveStatus(anyLong(), anyBoolean(), any());

		ResultActions perform =
				mockMvc.perform(
						patch("/api/v1/calendars/{calendarId}/monthly-goals/{id}/achieve", 1L, 1L)
								.headers(authorizationHeader())
								.queryParam("status", "true"));

		perform.andExpect(status().isNotFound());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-change-achieve-status-not-found",
								getDocumentResponse()));
	}

	@Test
	@DisplayName("특정 id의 월 목표를 삭제한다.")
	void monthlyGoalRemoveTest() throws Exception {

		willDoNothing().given(monthlyGoalUpdateService).remove(anyLong(), any());

		ResultActions perform =
				mockMvc.perform(
						delete("/api/v1/calendars/{calendarId}/monthly-goals/{id}", 1L, 1L)
								.headers(authorizationHeader()));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-remove",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("삭제할 월 목표 id"),
										parameterWithName("calendarId").description("캘린더 id"))));
	}
}
