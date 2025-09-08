package com.sillim.recordit.calendar.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryModifyRequest;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.service.CalendarCategoryCommandService;
import com.sillim.recordit.calendar.service.CalendarCategoryQueryService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CalendarCategoryController.class)
class CalendarCategoryControllerTest extends RestDocsTest {

	@MockBean
	CalendarCategoryQueryService calendarCategoryQueryService;
	@MockBean
	CalendarCategoryCommandService calendarCategoryCommandService;

	@Test
	@DisplayName("캘린더 카테고리 리스트를 조회한다.")
	void calendarCategoryList() throws Exception {
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		given(calendarCategoryQueryService.searchCalendarCategories(any())).willReturn(List.of(category));

		ResultActions perform = mockMvc.perform(get("/api/v1/calendar-categories"));

		perform.andExpect(status().isOk());

		perform.andDo(print()).andDo(document("calendar-category-list", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 카테고리를 생성한다.")
	void addCalendarCategory() throws Exception {
		CalendarCategoryAddRequest request = new CalendarCategoryAddRequest("aabbff", "name");

		ResultActions perform = mockMvc.perform(
				post("/api/v1/calendar-categories").contentType(MediaType.APPLICATION_JSON).content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print()).andDo(document("add-calendar-category", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 카테고리를 수정한다.")
	void modifyCalendarCategory() throws Exception {
		CalendarCategoryModifyRequest request = new CalendarCategoryModifyRequest("aabbff", "name");

		ResultActions perform = mockMvc.perform(put("/api/v1/calendar-categories/{categoryId}", 1L)
				.contentType(MediaType.APPLICATION_JSON).content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print()).andDo(document("modify-calendar-category", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 카테고리를 삭제한다.")
	void deleteCalendarCategory() throws Exception {
		ResultActions perform = mockMvc.perform(delete("/api/v1/calendar-categories/{categoryId}", 1L));

		perform.andExpect(status().isNoContent());

		perform.andDo(print()).andDo(document("delete-calendar-category", getDocumentRequest(), getDocumentResponse()));
	}
}
