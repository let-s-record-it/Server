package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.ScheduleAddRequest;
import com.sillim.recordit.schedule.repository.ScheduleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleGroupService {

    private final ScheduleGroupRepository scheduleGroupRepository;
    private final CalendarService calendarService;
    private final MemberQueryService memberQueryService;

    public ScheduleGroup addScheduleGroup(Boolean isRepeated, Long calendarId, Long memberId) {
        return scheduleGroupRepository.save(ScheduleGroup.builder()
                .isRepeated(isRepeated)
                .calendar(calendarService.findByCalendarId(calendarId))
                .member(memberQueryService.findByMemberId(memberId))
                .build());
    }
}
