package com.sillim.recordit.schedule.service;

import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.repository.ScheduleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleGroupService {

	private final ScheduleGroupRepository scheduleGroupRepository;

	public ScheduleGroup newScheduleGroup(Boolean isRepeated) {
		return scheduleGroupRepository.save(new ScheduleGroup(isRepeated));
	}
}
