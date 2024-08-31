package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyGoalUpdateService {

	private final WeeklyGoalQueryService weeklyGoalQueryService;
	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final MemberQueryService memberQueryService;
	private final WeeklyGoalRepository weeklyGoalRepository;

	public Long addWeeklyGoal(final WeeklyGoalUpdateRequest request, final Long memberId) {

		Member member = memberQueryService.findByMemberId(memberId);
		if (request.relatedMonthlyGoalId() == null) {
			return weeklyGoalRepository.save(request.toEntity(member)).getId();
		}
		MonthlyGoal relatedMonthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(
						request.relatedMonthlyGoalId(), memberId);
		return weeklyGoalRepository
				.save(
						request.toEntity(
								relatedMonthlyGoal, memberQueryService.findByMemberId(memberId)))
				.getId();
	}

	public void modifyWeeklyGoal(
			final WeeklyGoalUpdateRequest request, final Long weeklyGoalId, final Long memberId) {

		WeeklyGoal weeklyGoal =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		if (request.relatedMonthlyGoalId() == null) {
			weeklyGoal.modify(
					request.title(),
					request.description(),
					request.week(),
					request.startDate(),
					request.endDate(),
					request.colorHex());
			return;
		}
		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(
						request.relatedMonthlyGoalId(), memberId);
		weeklyGoal.modify(
				request.title(),
				request.description(),
				request.week(),
				request.startDate(),
				request.endDate(),
				request.colorHex(),
				monthlyGoal);
	}

	public void changeAchieveStatus(
			final Long weeklyGoalId, final Boolean status, final Long memberId) {

		WeeklyGoal weeklyGoal =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		weeklyGoal.changeAchieveStatus(status);
	}

	public void remove(final Long weeklyGoalId, final Long memberId) {

		WeeklyGoal weeklyGoal =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		weeklyGoal.remove();
	}

	public void linkRelatedMonthlyGoal(
			final Long weeklyGoalId, final Long monthlyGoalId, final Long memberId) {

		WeeklyGoal weeklyGoal =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
		weeklyGoal.linkRelatedMonthlyGoal(monthlyGoal);
	}
}
