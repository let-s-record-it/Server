package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
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

	public Long add(final MonthlyGoalUpdateRequest request, final Long memberId) {

		MonthlyGoal monthlyGoal =
				monthlyGoalRepository.save(
						request.toEntity(memberQueryService.findByMemberId(memberId)));
		return monthlyGoal.getId();
	}

	public void modify(
			final MonthlyGoalUpdateRequest request, final Long monthlyGoalId, final Long memberId) {

		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
		monthlyGoal.modify(
				request.title(),
				request.description(),
				request.startDate(),
				request.endDate(),
				request.colorHex());
	}

	public void changeAchieveStatus(
			final Long monthlyGoalId, final Boolean status, final Long memberId) {

		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
		monthlyGoal.changeAchieveStatus(status);
	}

	public void remove(final Long monthlyGoalId, final Long memberId) {

		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
		monthlyGoal.remove();
	}
}
