package com.sillim.recordit.calendar.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CalendarController.class)
class CalendarControllerTest extends RestDocsTest {

	@MockBean CalendarService calendarService;

	Member member;

	@BeforeEach
	void initObjects() {
		member =
				Member.builder()
						.auth(new Auth("1234567", OAuthProvider.KAKAO))
						.name("name")
						.job("job")
						.deleted(false)
						.memberRole(List.of(MemberRole.ROLE_USER))
						.build();
	}

	@Test
	@DisplayName("캘린더 목록을 조회한다.")
	void calendarList() throws Exception {
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByMemberId(any())).willReturn(List.of(calendar));

		ResultActions perform = mockMvc.perform(get("/api/v1/calendars"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("calendar-list", getDocumentRequest(), getDocumentResponse()));
	}
  
  @Test
	@DisplayName("캘린더를 추가한다.")
	void addCalendar() throws Exception {
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);
		CalendarAddRequest request = new CalendarAddRequest("calendar1", "aabbff");
		given(calendarService.addCalendar(any(CalendarAddRequest.class), any()))
				.willReturn(calendar);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/calendars")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print())
				.andDo(document("add-calendar", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더를 삭제한다.")
	void deleteCalendar() throws Exception {
		long calendarId = 1L;
		willDoNothing().given(calendarService).deleteByCalendarId(any(), any());

		ResultActions perform =
				mockMvc.perform(delete("/api/v1/calendars/{calendarId}", calendarId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("calendar-delete", getDocumentRequest(), getDocumentResponse()));
  }

}
