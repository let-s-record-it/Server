package com.sillim.recordit.task.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionAddRequest;
import com.sillim.recordit.task.fixture.TaskFixture;
import com.sillim.recordit.task.service.TaskCommandService;
import com.sillim.recordit.task.service.TaskQueryService;
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

@WebMvcTest(TaskController.class)
public class TaskControllerTest extends RestDocsTest {

	@MockBean TaskCommandService taskCommandService;
	@MockBean TaskQueryService taskQueryService;

	private Member member;
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
		taskGroup = new TaskGroup(false, null, null);
	}

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

	@Test
	@DisplayName("해당 날짜의 할 일 목록을 조회한다.")
	void getTaskListTest() throws Exception {

		Long calendarId = 1L;
		LocalDate date = LocalDate.of(2024, 6, 12);
		List<Task> tasks =
				LongStream.rangeClosed(1, 2)
						.mapToObj(
								id -> {
									Task task =
											spy(
													TaskFixture.DEFAULT.getWithDate(
															date, calendar, taskGroup));
									given(task.getId()).willReturn(id);
									return task;
								})
						.toList();

		given(taskQueryService.searchAllByDate(eq(calendarId), eq(date), any())).willReturn(tasks);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/tasks", calendarId)
								.contentType(MediaType.APPLICATION_JSON)
								.queryParam("date", date.toString()));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].id").value(tasks.get(0).getId()))
				.andExpect(jsonPath("$[0].title").value(tasks.get(0).getTitle()))
				.andExpect(jsonPath("$[0].achieved").value(tasks.get(0).isAchieved()))
				.andExpect(jsonPath("$[1].id").value(tasks.get(1).getId()))
				.andExpect(jsonPath("$[1].title").value(tasks.get(1).getTitle()))
				.andExpect(jsonPath("$[1].achieved").value(tasks.get(1).isAchieved()));

		perform.andDo(print())
				.andDo(
						document(
								"get-task-list",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID")),
								queryParameters(parameterWithName("date").description("조회할 날짜"))));
	}
}
