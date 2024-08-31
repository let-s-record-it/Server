package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.goal.service.WeeklyGoalQueryService;
import com.sillim.recordit.goal.service.WeeklyGoalUpdateService;
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

@WebMvcTest(WeeklyGoalController.class)
public class WeeklyGoalControllerTest extends RestDocsTest {

	@MockBean WeeklyGoalUpdateService weeklyGoalUpdateService;
	@MockBean WeeklyGoalQueryService weeklyGoalQueryService;

	private Member member;

	@BeforeEach
	void beforeEach() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("새로운 주 목표를 추가한다.")
	void weeklyGoalAddTest() throws Exception {

		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef",
						1L);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/goals/weeks")
								.headers(authorizationHeader())
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-add",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc())));
	}

	@Test
	@DisplayName("당월의 주 목표 목록을 조회한다.")
	void weeklyGoalList() throws Exception {

		WeeklyGoal expected = WeeklyGoalFixture.DEFAULT.getWithMember(member);

		List<WeeklyGoal> weeklyGoals =
				LongStream.rangeClosed(1, 3)
						.mapToObj(
								(id) -> {
									WeeklyGoal goal =
											spy(WeeklyGoalFixture.DEFAULT.getWithMember(member));
									given(goal.getId()).willReturn(id);
									given(goal.isAchieved()).willReturn(id % 2 == 0);
									return goal;
								})
						.toList();

		given(weeklyGoalQueryService.searchAllWeeklyGoalByDate(anyInt(), anyInt(), any()))
				.willReturn(weeklyGoals);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals/weeks")
								.headers(authorizationHeader())
								.queryParam("year", "2024")
								.queryParam("month", "8"));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].week").value(expected.getWeek()))
				.andExpect(jsonPath("$.[0].weeklyGoals.size()").value(3))
				.andExpect(jsonPath("$.[0].weeklyGoals[0].id").value(1))
				.andExpect(jsonPath("$.[0].weeklyGoals[0].title").value(expected.getTitle()))
				.andExpect(jsonPath("$.[0].weeklyGoals[0].achieved").value(false))
				.andExpect(jsonPath("$.[0].weeklyGoals[1].id").value(2))
				.andExpect(jsonPath("$.[0].weeklyGoals[1].title").value(expected.getTitle()))
				.andExpect(jsonPath("$.[0].weeklyGoals[1].achieved").value(true))
				.andExpect(jsonPath("$.[0].weeklyGoals[2].id").value(3))
				.andExpect(jsonPath("$.[0].weeklyGoals[2].title").value(expected.getTitle()))
				.andExpect(jsonPath("$.[0].weeklyGoals[2].achieved").value(false));

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-list",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								queryParameters(
										parameterWithName("year").description("조회할 연도"),
										parameterWithName("month").description("조회할 월"))));
	}

	@Test
	@DisplayName("두 개월에 걸쳐있는 주 목표가 있는 경우, 두번째 월로 검색했을 때 해당 주 목표를 첫 주차로 반환한다.")
	void weeklyGoalListWhenStuckInTwoMonths() throws Exception {

		Integer expectedWeek = 1;
		List<WeeklyGoal> weeklyGoals =
				List.of(
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								5, LocalDate.of(2024, 9, 29), LocalDate.of(2024, 10, 5), member));

		given(weeklyGoalQueryService.searchAllWeeklyGoalByDate(anyInt(), anyInt(), any()))
				.willReturn(weeklyGoals);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals/weeks")
								.headers(authorizationHeader())
								.queryParam("year", "2024")
								.queryParam("month", "8"));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].week").value(expectedWeek));

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-list-when-stuck-in-two-months",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								queryParameters(
										parameterWithName("year").description("조회할 연도"),
										parameterWithName("month").description("조회할 월"))));
	}

	@Test
	@DisplayName("특정 id의 주 목표를 상세하게 조회한다.")
	void weeklyGoalDetailsTest() throws Exception {

		WeeklyGoal weeklyGoal = spy(WeeklyGoalFixture.DEFAULT.getWithMember(member));
		given(weeklyGoal.getId()).willReturn(1L);

		given(weeklyGoalQueryService.searchByIdAndCheckAuthority(anyLong(), any()))
				.willReturn(weeklyGoal);

		ResultActions perform =
				mockMvc.perform(get("/api/v1/goals/weeks/{id}", 1L).headers(authorizationHeader()));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(weeklyGoal.getId()))
				.andExpect(jsonPath("$.title").value(weeklyGoal.getTitle()))
				.andExpect(jsonPath("$.week").value(weeklyGoal.getWeek()))
				.andExpect(jsonPath("$.startDate").value(weeklyGoal.getStartDate().toString()))
				.andExpect(jsonPath("$.endDate").value(weeklyGoal.getEndDate().toString()))
				.andExpect(jsonPath("$.description").value(weeklyGoal.getDescription()))
				.andExpect(jsonPath("$.colorHex").value(weeklyGoal.getColorHex()));

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-details",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("조회할 주 목표 id"))));
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 수정한다.")
	void modifyWeeklyGoal() throws Exception {

		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!(수정)",
						"취업할 때까지 숨 참는다.(수정)",
						4,
						LocalDate.of(2024, 8, 18),
						LocalDate.of(2024, 8, 24),
						"ff123456",
						1L);

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/goals/weeks/{id}", 1L)
								.headers(authorizationHeader())
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-modify",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("수정할 주 목표 id"))));
	}

	@Test
	@DisplayName("id에 해당하는 주 목표의 달성 상태를 변경한다.")
	void weeklyGoalChangeAchieveStatus() throws Exception {

		ResultActions perform =
				mockMvc.perform(
						patch("/api/v1/goals/weeks/{id}/achieve", 1L)
								.headers(authorizationHeader())
								.queryParam("status", "true"));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-change-achieve-status",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("달성 상태를 변경할 주 목표 id")),
								queryParameters(
										parameterWithName("status")
												.description("달성 상태(false, true)"))));
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 삭제한다.")
	void removeWeeklyGoal() throws Exception {

		ResultActions perform =
				mockMvc.perform(
						delete("/api/v1/goals/weeks/{id}", 1L).headers(authorizationHeader()));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-remove",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("삭제할 주 목표 id"))));
	}

	@Test
	@DisplayName("id에 해당하는 주 목표를 월 목표와 연결한다.")
	void linkRelatedGoal() throws Exception {

		ResultActions perform =
				mockMvc.perform(
						patch("/api/v1/goals/weeks/{id}/link", 1L)
								.headers(authorizationHeader())
								.queryParam("relatedGoalId", "1"));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"weekly-goal-link-related-goal",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								pathParameters(
										parameterWithName("id").description("연관 목표를 연결할 주 목표 id")),
								queryParameters(
										parameterWithName("relatedGoalId")
												.description("주 목표와 연결할 월 목표 id"))));
	}
}
