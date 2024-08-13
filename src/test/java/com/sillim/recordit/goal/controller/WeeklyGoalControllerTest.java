package com.sillim.recordit.goal.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.service.WeeklyGoalUpdateService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(WeeklyGoalController.class)
public class WeeklyGoalControllerTest extends RestDocsTest {

	@MockBean WeeklyGoalUpdateService weeklyGoalUpdateService;

	@Test
	@DisplayName("새로운 주 목표를 추가한다.")
	void monthlyGoalAddTest() throws Exception {

		WeeklyGoalUpdateRequest request =
				new WeeklyGoalUpdateRequest(
						"취뽀하기!",
						"취업할 때까지 숨 참는다.",
						3,
						LocalDate.of(2024, 8, 11),
						LocalDate.of(2024, 8, 17),
						"ff83c8ef");

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
}
