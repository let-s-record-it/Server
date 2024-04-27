package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.repository.ScheduleGroupRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleGroupServiceTest {

    @Mock
    ScheduleGroupRepository scheduleGroupRepository;
    @Mock
    CalendarService calendarService;
    @Mock
    MemberQueryService memberQueryService;
    @InjectMocks
    ScheduleGroupService scheduleGroupService;

    Member member;
    Calendar calendar;
    long calendarId = 1L;
    long memberId = 1L;

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
    @DisplayName("스케줄 그룹을 추가할 수 있다.")
    void addScheduleGroup() {
        ScheduleGroup expectScheduleGroup = ScheduleGroup.builder()
                .isRepeated(false)
                .calendar(calendar)
                .member(member)
                .build();
        given(scheduleGroupRepository.save(any(ScheduleGroup.class))).willReturn(expectScheduleGroup);

        ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(false, calendarId, memberId);

        assertAll(() -> {
            assertThat(scheduleGroup.getIsRepeated()).isEqualTo(false);
            assertThat(scheduleGroup.getMember()).isEqualTo(member);
            assertThat(scheduleGroup.getCalendar()).isEqualTo(calendar);
        });
    }
}