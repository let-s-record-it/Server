package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyGoalUpdateService {

	private final MemberQueryService memberQueryService;
	private final WeeklyGoalRepository weeklyGoalRepository;

	public Long addWeeklyGoal(final WeeklyGoalUpdateRequest request, final Long memberId) {
		WeeklyGoal saved =
				weeklyGoalRepository.save(
						request.toEntity(memberQueryService.findByMemberId(memberId)));
		return saved.getId();
	}
}
