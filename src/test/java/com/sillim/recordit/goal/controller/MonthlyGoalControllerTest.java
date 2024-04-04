package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MonthlyGoalController.class)
public class MonthlyGoalControllerTest extends RestDocsTest {

	@MockBean MonthlyGoalUpdateService monthlyGoalUpdateService;

	@Test
	@DisplayName("월 목표 추가 API")
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
	@DisplayName("월 목표 수정 API")
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
	@DisplayName("월 목표 수정 API - MonthlyGoal Not Found")
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
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(authorizationDesc())));
	}
}
