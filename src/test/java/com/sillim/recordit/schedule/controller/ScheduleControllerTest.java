package com.sillim.recordit.schedule.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.ScheduleAddRequest;
import com.sillim.recordit.schedule.service.ScheduleService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest extends RestDocsTest {

    @MockBean
    ScheduleService scheduleService;

    Member member;
    Calendar calendar;

    @BeforeEach
    void initObjects() {
        member = Member.builder()
                .auth(new Auth("1234567", OAuthProvider.KAKAO))
                .name("name")
                .job("job")
                .deleted(false)
                .memberRole(List.of(MemberRole.ROLE_USER))
                .build();
        calendar = Calendar.builder()
                .title("calendar1")
                .colorHex("#aabbff")
                .member(member)
                .build();
    }

    @Test
    @DisplayName("스케줄을 생성한다.")
    void addSchedule() throws Exception {
        long calendarId = 1L;
        long memberId = 1L;
        ScheduleAddRequest scheduleAddRequest = new ScheduleAddRequest("title", "description",
                false, LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 2, 1, 0, 0), false, null, "#aaffbb", "서울역", true, 36.0,
                127.0, true, LocalDateTime.of(2024, 1, 1, 0, 0), calendarId);
        ScheduleGroup scheduleGroup = ScheduleGroup.builder()
                .isRepeated(false)
                .member(member)
                .calendar(calendar).build();
        Schedule schedule = Schedule.builder()
                .title("title")
                .description("description")
                .isAllDay(false)
                .startDatetime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endDatetime(LocalDateTime.of(2024, 2, 1, 0, 0))
                .colorHex("#aaffbb")
                .setLocation(true)
                .place("서울역")
                .latitude(36.0)
                .longitude(127.0)
                .setAlarm(true)
                .alarmTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .scheduleGroup(scheduleGroup)
                .build();
        given(scheduleService.addSchedules(scheduleAddRequest, memberId))
                .willReturn(List.of(schedule));

        ResultActions perform = mockMvc.perform(post("/api/v1/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(scheduleAddRequest)));

        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(document("add-schedule", getDocumentRequest(), getDocumentResponse()));
    }
}