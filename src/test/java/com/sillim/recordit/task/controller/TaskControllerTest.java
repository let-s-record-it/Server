package com.sillim.recordit.task.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.support.restdocs.RestDocsTest;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.dto.TaskAddRequest;
import com.sillim.recordit.task.dto.TaskRepetitionAddRequest;
import com.sillim.recordit.task.service.TaskCommandService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TaskController.class)
public class TaskControllerTest extends RestDocsTest {

	@MockBean TaskCommandService taskCommandService;

	@Test
	@DisplayName("할 일을 생성한다.")
	void addNonRepeatingTaskTest() throws Exception {

		TaskRepetitionAddRequest repetitionRequest =
				new TaskRepetitionAddRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);
		TaskAddRequest request =
				new TaskAddRequest(
						"회의록 작성",
						"프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 1),
						"ff40d974",
						true,
						repetitionRequest,
						null,
						null);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/calendars/{calendarId}/tasks", 1L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print())
				.andDo(
						document(
								"add-tasks",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"))));
	}
}
