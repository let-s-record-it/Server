package com.sillim.recordit.calendar.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarModifyRequest;
import com.sillim.recordit.calendar.dto.request.JoinInCalendarRequest;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.dto.response.CalendarResponse;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarCommandService;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.JoinCalendarService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.service.MemberQueryService;
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

	@MockBean CalendarCommandService calendarCommandService;
	@MockBean CalendarMemberService calendarMemberService;
	@MockBean MemberQueryService memberQueryService;
	@MockBean JoinCalendarService joinCalendarService;

	Member member;
	long memberId = 1L;

	@BeforeEach
	void initObjects() {
		member =
				Member.createNoJobMember(
						"1234567", OAuthProvider.KAKAO, "name", "m@mail.com", "http://image.com");
	}

	@Test
	@DisplayName("캘린더 목록을 조회한다.")
	void calendarList() throws Exception {
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		CalendarResponse calendarResponse = CalendarResponse.from(calendar);
		given(calendarMemberService.searchCalendarsByMemberId(any()))
				.willReturn(List.of(calendarResponse));

		ResultActions perform = mockMvc.perform(get("/api/v1/calendars"));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(document("calendar-list", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더를 추가한다.")
	void addCalendar() throws Exception {
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		CalendarAddRequest request = new CalendarAddRequest("calendar1", 1L);
		given(calendarCommandService.addCalendar(any(CalendarAddRequest.class), any()))
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
	@DisplayName("캘린더를 수정한다.")
	void modifyCalendar() throws Exception {
		long calendarId = 1L;
		CalendarModifyRequest request = new CalendarModifyRequest("calendar1", 1L);

		ResultActions perform =
				mockMvc.perform(
						put("/api/v1/calendars/{calendarId}", calendarId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("modify-calendar", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더를 삭제한다.")
	void deleteCalendar() throws Exception {
		long calendarId = 1L;
		willDoNothing().given(calendarCommandService).removeByCalendarId(any(), any());

		ResultActions perform =
				mockMvc.perform(delete("/api/v1/calendars/{calendarId}", calendarId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(document("calendar-delete", getDocumentRequest(), getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 멤버 목록을 조회한다.")
	void calendarMemberList() throws Exception {
		long calendarId = 1L;
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		CalendarMember calendarMember = new CalendarMember(calendar, memberId);
		CalendarMemberResponse calendarMemberResponse =
				CalendarMemberResponse.of(calendarMember, member);
		given(calendarMemberService.searchCalendarMembers(eq(calendarId)))
				.willReturn(List.of(calendarMemberResponse));

		ResultActions perform =
				mockMvc.perform(get("/api/v1/calendars/{calendarId}/members", calendarId));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"calendar-member-list",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 멤버를 조회한다.")
	void calendarMemberDetails() throws Exception {
		long calendarId = 1L;
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		CalendarMember calendarMember = new CalendarMember(calendar, memberId);
		given(calendarMemberService.searchCalendarMember(eq(calendarId), eq(memberId)))
				.willReturn(calendarMember);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);

		ResultActions perform =
				mockMvc.perform(
						get(
								"/api/v1/calendars/{calendarId}/members/{memberId}",
								calendarId,
								memberId));

		perform.andExpect(status().isOk());

		perform.andDo(print())
				.andDo(
						document(
								"calendar-member-details",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더 멤버를 삭제한다.")
	void calendarMemberDelete() throws Exception {
		long calendarId = 1L;
		long memberId = 1L;

		ResultActions perform =
				mockMvc.perform(
						delete(
								"/api/v1/calendars/{calendarId}/members/{memberId}",
								calendarId,
								memberId));

		perform.andExpect(status().isNoContent());

		perform.andDo(print())
				.andDo(
						document(
								"calendar-member-delete",
								getDocumentRequest(),
								getDocumentResponse()));
	}

	@Test
	@DisplayName("캘린더에 참가한다.")
	void joinInCalendar() throws Exception {
		String inviteCode = "inviteCode";
		long calendarMemberId = 1L;
		JoinInCalendarRequest request = new JoinInCalendarRequest(inviteCode);
		given(joinCalendarService.joinInCalendar(eq(inviteCode), any()))
				.willReturn(calendarMemberId);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/calendars/join")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(request)));

		perform.andExpect(status().isCreated());

		perform.andDo(print())
				.andDo(document("join-in-calendar", getDocumentRequest(), getDocumentResponse()));
	}
}
