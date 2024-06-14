package com.sillim.recordit.schedule.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleAlarm;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.dto.response.RepetitionPatternResponse;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;
    private final CalendarService calendarService;
    private final RepetitionPatternService repetitionPatternService;
    private final ScheduleAlarmService scheduleAlarmService;

    public DayScheduleResponse searchSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository
                .findByScheduleId(scheduleId)
                .orElseThrow(() -> new RecordNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        validateAuthenticatedUser(memberId, schedule.getCalendar().getMember().getId());
        List<LocalDateTime> alarmTimes = scheduleAlarmService.searchByScheduleId(scheduleId)
                .stream()
                .map(ScheduleAlarm::getAlarmTime).toList();

        if (schedule.getScheduleGroup().getIsRepeated()) {
            return DayScheduleResponse.of(
                    schedule,
                    true,
                    alarmTimes,
                    RepetitionPatternResponse.from(
                           repetitionPatternService.searchByScheduleGroupId(
                                    schedule.getScheduleGroup().getId())));
        }

        return DayScheduleResponse.of(schedule, false, alarmTimes, null);
    }

    public List<MonthScheduleResponse> searchSchedulesInMonth(Long calendarId, Integer year,
            Integer month,
            Long memberId) {
        validateAuthenticatedUser(memberId,
                calendarService.searchByCalendarId(calendarId).getMember().getId());
        return scheduleRepository.findScheduleInMonth(calendarId, year, month)
                .stream()
                .map(MonthScheduleResponse::from)
                .toList();
    }

    public List<DayScheduleResponse> searchSchedulesInDay(Long calendarId, LocalDate date,
            Long memberId) {
        validateAuthenticatedUser(memberId,
                calendarService.searchByCalendarId(calendarId).getMember().getId());
        return scheduleRepository.findScheduleInDay(calendarId, date).stream()
                .map(
                        schedule ->
                                DayScheduleResponse.of(
                                        schedule,
                                        schedule.getScheduleGroup().getIsRepeated(),
                                        scheduleAlarmService.searchByScheduleId(schedule.getId())
                                                .stream()
                                                .map(ScheduleAlarm::getAlarmTime)
                                                .toList(),
                                        schedule.getScheduleGroup()
                                                .getRepetitionPattern()
                                                .map(RepetitionPatternResponse::from)
                                                .orElse(null)))
                .toList();
    }

    private void validateAuthenticatedUser(Long principalId, Long memberId) {
        if (!principalId.equals(memberId)) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
        }
    }
}
