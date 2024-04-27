package com.sillim.recordit.schedule.service;

import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.ScheduleAddRequest;
import com.sillim.recordit.schedule.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleGroupService scheduleGroupService;
    private final RepetitionPatternService repetitionPatternService;

    public List<Schedule> addSchedules(ScheduleAddRequest request, Long memberId) {
        ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(request.isRepeated(),
                request.calendarId(), memberId);

        if (request.isRepeated()) {
            return addRepeatingSchedule(request, scheduleGroup);
        }
        return List.of(scheduleRepository.save(request.toSchedule(scheduleGroup)));
    }

    private List<Schedule> addRepeatingSchedule(ScheduleAddRequest request,
            ScheduleGroup scheduleGroup) {
        return repetitionPatternService.addRepetitionPattern(request.repetition(), scheduleGroup)
                .repeatingStream()
                .map(temporalAmount -> scheduleRepository.save(
                        request.toSchedule(temporalAmount, scheduleGroup)))
                .toList();
    }
}
