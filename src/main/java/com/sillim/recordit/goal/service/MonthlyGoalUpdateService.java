package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
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
	private final MonthlyGoalRepository monthlyGoalRepository;

	// TODO final 붙이기
	public void add(final MonthlyGoalUpdateRequest request, final Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		monthlyGoalRepository.save(request.toEntity(member));
	}

	public void modify(
			final MonthlyGoalUpdateRequest request, final Long monthlyGoalId, final Long memberId) {

		MonthlyGoal monthlyGoal = monthlyGoalQueryService.search(monthlyGoalId, memberId);
		monthlyGoal.modify(
				request.title(),
				request.description(),
				request.startDate(),
				request.endDate(),
				request.colorHex());
	}
}
