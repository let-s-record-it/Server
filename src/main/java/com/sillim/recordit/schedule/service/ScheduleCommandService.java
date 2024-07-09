package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.RepetitionUpdateRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleModifyRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleCommandService {

    private static final long TO_SKIP_ADD_TEMPORAL = 0L;
    private static final long TO_SKIP_MODIFY_TEMPORAL = 1L;

    private final ScheduleRepository scheduleRepository;
    private final CalendarService calendarService;
    private final ScheduleGroupService scheduleGroupService;
    private final RepetitionPatternService repetitionPatternService;

    public List<Schedule> addSchedules(ScheduleAddRequest request, Long calendarId) {
        ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(request.isRepeated());

        if (request.isRepeated()) {
            Calendar calendar = calendarService.searchByCalendarId(calendarId);
            return addRepeatingSchedule(
                    temporalAmount ->
                            scheduleRepository.save(
                                    request.toSchedule(temporalAmount, calendar, scheduleGroup)),
                    request.repetition(),
                    scheduleGroup,
                    TO_SKIP_ADD_TEMPORAL);
        }

        Schedule schedule =
                request.toSchedule(calendarService.searchByCalendarId(calendarId), scheduleGroup);
        return List.of(scheduleRepository.save(schedule));
    }

    public void modifySchedule(
            ScheduleModifyRequest request, Long scheduleId, Long memberId) {
        Schedule schedule =
                scheduleRepository
                        .findByScheduleId(scheduleId)
                        .orElseThrow(
                                () -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.validateAuthenticatedMember(memberId);
        Calendar calendar = calendarService.searchByCalendarId(request.calendarId());
        calendar.validateAuthenticatedMember(memberId);
        ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(request.isRepeated());

        schedule.modify(
                request.title(),
                request.description(),
                request.isAllDay(),
                request.startDateTime(),
                request.endDateTime(),
                request.colorHex(),
                request.place(),
                request.setLocation(),
                request.latitude(),
                request.longitude(),
                request.setAlarm(),
                request.alarmTimes(),
                calendar,
                scheduleGroup);

        if (request.isRepeated()) {
            addRepeatingSchedule(
                    temporalAmount ->
                            scheduleRepository.save(
                                    request.toSchedule(temporalAmount, calendar, scheduleGroup)),
                    request.repetition(),
                    scheduleGroup,
                    TO_SKIP_MODIFY_TEMPORAL);
        }
    }

    public void modifySchedulesInGroup(
            ScheduleModifyRequest request, Long scheduleId, Long memberId) {
        Schedule schedule =
                scheduleRepository
                        .findByScheduleId(scheduleId)
                        .orElseThrow(
                                () -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.validateAuthenticatedMember(memberId);
        Calendar calendar = calendarService.searchByCalendarId(request.calendarId());
        calendar.validateAuthenticatedMember(memberId);

        ScheduleGroup scheduleGroup = schedule.getScheduleGroup();
        scheduleRepository.findSchedulesInGroup(scheduleGroup.getId()).forEach(Schedule::delete);

        if (request.isRepeated()) {
            repetitionPatternService.updateRepetitionPattern(request.repetition(), scheduleGroup)
                    .repeatingStream()
                    .forEach(temporalAmount -> scheduleRepository.save(
                            request.toSchedule(temporalAmount, calendar, scheduleGroup)));
        } else {
            scheduleGroup.modifyNotRepeated();
        }
    }

    private List<Schedule> addRepeatingSchedule(
            Function<TemporalAmount, Schedule> temporalToSchedule,
            RepetitionUpdateRequest repetition,
            ScheduleGroup scheduleGroup,
            long toSkipTemporal) {
        return repetitionPatternService
                .addRepetitionPattern(repetition, scheduleGroup)
                .repeatingStream()
                .skip(toSkipTemporal)
                .map(temporalToSchedule)
                .toList();
    }

    public void removeSchedule(Long scheduleId, Long memberId) {
        Schedule schedule =
                scheduleRepository
                        .findByScheduleId(scheduleId)
                        .orElseThrow(
                                () -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.validateAuthenticatedMember(memberId);

        schedule.delete();
    }

    public void removeSchedulesInGroup(Long scheduleId, Long memberId) {
        Schedule schedule =
                scheduleRepository
                        .findByScheduleId(scheduleId)
                        .orElseThrow(
                                () -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.validateAuthenticatedMember(memberId);

        scheduleRepository
                .findSchedulesInGroup(schedule.getScheduleGroup().getId())
                .forEach(Schedule::delete);
    }

    public void removeSchedulesInGroupAfter(Long scheduleId, Long memberId) {
        Schedule schedule =
                scheduleRepository
                        .findByScheduleId(scheduleId)
                        .orElseThrow(
                                () -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.validateAuthenticatedMember(memberId);

        scheduleRepository
                .findSchedulesInGroupAfter(
                        schedule.getScheduleGroup().getId(), schedule.getStartDateTime())
                .forEach(Schedule::delete);
    }
}
