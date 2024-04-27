package com.sillim.recordit.schedule.controller;

import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.dto.ScheduleAddRequest;
import com.sillim.recordit.schedule.service.ScheduleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<List<Schedule>> addSchedules(
            @Validated @RequestBody ScheduleAddRequest request, @AuthenticationPrincipal
    AuthorizedUser authorizedUser) {
        return ResponseEntity.ok(
                scheduleService.addSchedules(request, authorizedUser.getMemberId()));
    }
}
