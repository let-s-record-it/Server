package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.repository.ScheduleGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleGroupServiceTest {

	@Mock ScheduleGroupRepository scheduleGroupRepository;
	@InjectMocks ScheduleGroupService scheduleGroupService;

	@Test
	@DisplayName("스케줄 그룹을 추가할 수 있다.")
	void addScheduleGroup() {
		ScheduleGroup expectScheduleGroup = new ScheduleGroup(false);
		given(scheduleGroupRepository.save(any(ScheduleGroup.class)))
				.willReturn(expectScheduleGroup);

		ScheduleGroup scheduleGroup = scheduleGroupService.addScheduleGroup(false);

		assertThat(scheduleGroup.isRepeated()).isEqualTo(false);
	}
}
