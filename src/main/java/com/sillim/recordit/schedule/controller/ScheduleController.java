package com.sillim.recordit.schedule.controller;

import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.dto.response.RepetitionPatternResponse;
import com.sillim.recordit.schedule.service.RepetitionPatternService;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import com.sillim.recordit.schedule.service.ScheduleQueryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendars/{calendarId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleCommandService scheduleCommandService;
    private final ScheduleQueryService scheduleQueryService;
    private final RepetitionPatternService repetitionPatternService;

    @PostMapping
    public ResponseEntity<List<MonthScheduleResponse>> addSchedules(
            @Validated @RequestBody ScheduleAddRequest request, @PathVariable Long calendarId) {
        return ResponseEntity.ok(
                scheduleCommandService.addSchedules(request, calendarId).stream()
                        .map(MonthScheduleResponse::from)
                        .toList());
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<DayScheduleResponse> scheduleDetails(@PathVariable Long scheduleId) {
        Schedule schedule = scheduleQueryService.searchSchedule(scheduleId);

        if (schedule.getScheduleGroup().getIsRepeated()) {
            return ResponseEntity.ok(
                    DayScheduleResponse.of(
                            schedule,
                            true,
                            RepetitionPatternResponse.from(
                                    repetitionPatternService.searchByScheduleGroupId(
                                            schedule.getScheduleGroup().getId()))));
        }

        return ResponseEntity.ok(DayScheduleResponse.of(schedule, false, null));
    }

    @GetMapping("/month")
    public ResponseEntity<List<MonthScheduleResponse>> scheduleListInMonth(
            @PathVariable Long calendarId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(
                scheduleQueryService.searchSchedulesInMonth(calendarId, year, month).stream()
                        .map(MonthScheduleResponse::from)
                        .toList());
    }

    @GetMapping("/day")
    public ResponseEntity<List<DayScheduleResponse>> scheduleListInDay(
            @PathVariable Long calendarId,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(
                scheduleQueryService.searchSchedulesInDay(calendarId, date).stream()
                        .map(schedule -> DayScheduleResponse.of(schedule,
                                schedule.getScheduleGroup().getIsRepeated(),
                                RepetitionPatternResponse.from(
                                        schedule.getScheduleGroup().getRepetitionPattern())))
                        .toList());
    }
}
