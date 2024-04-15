package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.util.List;
import java.util.stream.LongStream;
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

	Member member = new Member();

	@Test
	@DisplayName("새로운 월 목표를 추가한다.")
	void monthlyGoalAddTest() throws Exception {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest("취뽀하기!", "취업할 때까지 숨 참는다.", 2024, 4, "#83c8ef");

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/goals/months")
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

	// TODO: Member Not Found 예외 추가

	@Test
	@DisplayName("기존의 월 목표를 수정한다.")
	void monthlyGoalModifyTest() throws Exception {

		MonthlyGoalUpdateRequest request =
				new MonthlyGoalUpdateRequest(
						"(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다.", 2024, 12, "#123456");

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/goals/months/{monthlyGoalId}", 1L)
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
						"(수정)취뽀하기!", "(수정)취업할 때까지 숨 참는다.", 2024, 12, "#123456");
		willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND))
				.given(monthlyGoalUpdateService)
				.modify(any(MonthlyGoalUpdateRequest.class), anyLong());

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/goals/months/{monthlyGoalId}", 1L)
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
											spy(MonthlyGoalFixture.DEFAULT.getWithMember(member));
									given(goal.getId()).willReturn(id);
									given(goal.getAchieved()).willReturn(id % 2 == 0);
									return goal;
								})
						.toList();

		given(monthlyGoalQueryService.searchAllByDate(anyInt(), anyInt(), anyLong()))
				.willReturn(monthlyGoals);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals/months")
								.headers(authorizationHeader())
								.queryParam("goalYear", "2024")
								.queryParam("goalMonth", "5"));

		perform.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3));

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-list",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc()),
								queryParameters(
										parameterWithName("goalYear").description("조회 연도"),
										parameterWithName("goalMonth").description("조회 월"))));
	}

	// TODO: Member Not Found 예외 추가

	@Test
	@DisplayName("특정 id의 월 목표를 상세하게 조회한다.")
	void monthlyGoalDetailsTest() throws Exception {

		MonthlyGoal monthlyGoal = spy(MonthlyGoalFixture.DEFAULT.getWithMember(member));
		given(monthlyGoal.getId()).willReturn(1L);

		given(monthlyGoalQueryService.search(anyLong())).willReturn(monthlyGoal);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals/months/{monthlyGoalId}", 1L)
								.headers(authorizationHeader()));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(monthlyGoal.getId()))
				.andExpect(jsonPath("$.title").value(monthlyGoal.getTitle()))
				.andExpect(jsonPath("$.goalYear").value(monthlyGoal.getGoalYear()))
				.andExpect(jsonPath("$.goalMonth").value(monthlyGoal.getGoalMonth()))
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
										parameterWithName("monthlyGoalId")
												.description("조회할 월 목표 id"))));
	}

	@Test
	@DisplayName("존재하지 않는 월 목표를 상세하게 조회할 경우 NOT FOUND 응답을 반환한다.")
	void monthlyGoalDetailsNotFoundTest() throws Exception {

		given(monthlyGoalQueryService.search(anyLong()))
				.willThrow(new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/goals/months/{monthlyGoalId}", 1L)
								.headers(authorizationHeader()));

		perform.andExpect(status().isNotFound());

		perform.andDo(print())
				.andDo(
						document(
								"monthly-goal-details-monthly-goal-not-found",
								getDocumentResponse()));
	}
}
