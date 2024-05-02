package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyGoalUpdateService {

	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final MemberQueryService memberQueryService;
	private final MonthlyGoalJpaRepository monthlyGoalJpaRepository;

	public void add(MonthlyGoalUpdateRequest request, Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		monthlyGoalJpaRepository.save(request.toEntity(member));
	}

	public void modify(MonthlyGoalUpdateRequest request, Long monthlyGoalId) {

		MonthlyGoal monthlyGoal = monthlyGoalQueryService.search(monthlyGoalId);
		monthlyGoal.modify(
				request.title(),
				request.description(),
				request.startDate(),
				request.endDate(),
				request.colorHex());
	}
}
