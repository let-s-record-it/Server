package com.sillim.recordit.task.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskGroupUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
import com.sillim.recordit.task.dto.response.TaskDetailsResponse;
import com.sillim.recordit.task.fixture.TaskFixture;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
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
	private CalendarCategory calendarCategory;
	private ScheduleCategory taskCategory;
	private Calendar calendar;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		taskCategory = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
	}

	@Test
	@DisplayName("할 일을 생성한다.")
	void addNonRepeatingTaskTest() throws Exception {

		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(null, null);
		TaskRepetitionUpdateRequest repetitionRequest =
				new TaskRepetitionUpdateRequest(
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
						true,
						1L,
						repetitionRequest,
						taskGroupRequest);

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
		TaskGroup taskGroup = new TaskGroup(null, null);
		LocalDate date = LocalDate.of(2024, 6, 12);
		List<Task> tasks =
				LongStream.rangeClosed(1, 2)
						.mapToObj(
								id -> {
									Task task =
											spy(
													TaskFixture.DEFAULT.getWithDate(
															date,
															taskCategory,
															calendar,
															taskGroup));
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

	@Test
	@DisplayName("해당 id의 할 일을 상세조회한다. (반복 없음)")
	void getTaskDetailsWithNoRepeat() throws Exception {
		Long calendarId = 1L;
		calendar = spy(calendar);
		given(calendar.getId()).willReturn(calendarId);
		TaskGroup taskGroup = new TaskGroup(null, null);
		Long taskId = 2L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);

		TaskDetailsResponse response = TaskDetailsResponse.from(task);
		given(taskQueryService.searchByIdAndCalendarId(eq(taskId), eq(calendarId), any()))
				.willReturn(response);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/tasks/{taskId}", calendarId, taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"get-task-details-with-no-repeat",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId").description("할 일 ID"))));
	}

	@Test
	@DisplayName("해당 id의 할 일을 상세조회한다. (반복 있음)")
	void getTaskDetailsWithRepeat() throws Exception {
		Long calendarId = 1L;
		calendar = spy(calendar);
		given(calendar.getId()).willReturn(calendarId);
		TaskGroup taskGroup = new TaskGroup(null, null);
		Long taskId = 2L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);
		TaskRepetitionPattern repetition = TaskRepetitionPatternFixture.DAILY.get(taskGroup);

		TaskDetailsResponse response = TaskDetailsResponse.of(task, repetition);
		given(taskQueryService.searchByIdAndCalendarId(eq(taskId), eq(calendarId), any()))
				.willReturn(response);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/tasks/{taskId}", calendarId, taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"get-task-details-with-repeat",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId").description("할 일 ID"))));
	}

	@Test
	@DisplayName("해당 id의 할 일을 상세조회한다. (연관 목표 있음)")
	void getTaskDetailsWithRelatedGoal() throws Exception {
		Long calendarId = 1L;
		calendar = spy(calendar);
		given(calendar.getId()).willReturn(calendarId);
		Long monthlyGoalId = 2L;
		MonthlyGoal monthlyGoal =
				spy(MonthlyGoalFixture.DEFAULT.getWithMember(taskCategory, member));
		given(monthlyGoal.getId()).willReturn(monthlyGoalId);
		TaskGroup taskGroup = new TaskGroup(monthlyGoal, null);
		Long taskId = 3L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);

		TaskDetailsResponse response = TaskDetailsResponse.from(task);
		given(taskQueryService.searchByIdAndCalendarId(eq(taskId), eq(calendarId), any()))
				.willReturn(response);

		ResultActions perform =
				mockMvc.perform(
						get("/api/v1/calendars/{calendarId}/tasks/{taskId}", calendarId, taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"get-task-details-with-related-goals",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId").description("할 일 ID"))));
	}

	@Test
	@DisplayName("선택한 할 일이 속한 그룹 내의 모든 할 일들을 수정한다.")
	void modifyAll() throws Exception {

		Long calendarId = 1L;
		Long taskId = 2L;
		TaskRepetitionUpdateRequest repetitionRequest =
				new TaskRepetitionUpdateRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);
		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(1L, null);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 7, 28),
						1L,
						3L,
						true,
						repetitionRequest,
						taskGroupRequest);

		ResultActions perform =
				mockMvc.perform(
						put(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/modify-all",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"modify-all-tasks",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId")
												.description("수정을 위해 선택한 할 일의 ID"))));
	}

	@Test
	@DisplayName("선택한 할 일의 내용을 수정한다.")
	void modifyOne() throws Exception {

		Long calendarId = 1L;
		Long taskId = 2L;
		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(1L, null);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 7, 28),
						1L,
						3L,
						false,
						null,
						taskGroupRequest);

		ResultActions perform =
				mockMvc.perform(
						put(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/modify-one",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"modify-one-task",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId")
												.description("수정을 위해 선택한 할 일의 ID"))));
	}

	@Test
	@DisplayName("선택한 할 일의 내용을 수정하고 반복하도록 만든다.")
	void modifyOneAndMakeRepeatable() throws Exception {

		Long calendarId = 1L;
		Long taskId = 2L;
		TaskRepetitionUpdateRequest repetitionRequest =
				new TaskRepetitionUpdateRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);
		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(1L, null);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 7, 28),
						1L,
						3L,
						true,
						repetitionRequest,
						taskGroupRequest);

		ResultActions perform =
				mockMvc.perform(
						put(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/modify-one",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print()).andDo(document("modify-one-task", getDocumentRequest()));
	}

	@Test
	@DisplayName("선택한 할 일의 속한 할 일 그룹 내의 모든 할 일을 삭제한다.")
	void deleteAllTasks() throws Exception {
		Long calendarId = 1L;
		Long taskId = 2L;

		ResultActions perform =
				mockMvc.perform(
						delete(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/remove-all",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"delete-all-tasks",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId")
												.description("삭제를 위해 선택한 할 일의 ID"))));
	}

	@Test
	@DisplayName("선택한 할 일이 속한 할 일 그룹 내의 모든 할 일 중 선택한 날짜 이후의 할 일을 삭제한다.")
	void deleteAfterAllTasks() throws Exception {
		Long calendarId = 1L;
		Long taskId = 2L;

		ResultActions perform =
				mockMvc.perform(
						delete(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/remove-after-all",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"delete-after-all-tasks",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId")
												.description("삭제를 위해 선택한 할 일의 ID"))));
	}

	@Test
	@DisplayName("선택한 할 일을 삭제한다.")
	void deleteOneTask() throws Exception {
		Long calendarId = 1L;
		Long taskId = 2L;

		ResultActions perform =
				mockMvc.perform(
						delete(
										"/api/v1/calendars/{calendarId}/tasks/{taskId}/remove-one",
										calendarId,
										taskId)
								.contentType(MediaType.APPLICATION_JSON));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"delete-one-task",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("calendarId").description("캘린더 ID"),
										parameterWithName("taskId")
												.description("삭제를 위해 선택한 할 일의 ID"))));
	}
}
