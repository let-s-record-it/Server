package com.sillim.recordit.goal.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyGoalQueryService {

	private final MemberQueryService memberQueryService;
	private final MonthlyGoalRepository monthlyGoalRepository;

	public MonthlyGoal search(final Long monthlyGoalId, final Long memberId) {

		final Member member = memberQueryService.findByMemberId(memberId);
		return monthlyGoalRepository
				.findByIdAndMember(monthlyGoalId, member)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));
	}

	public List<MonthlyGoal> searchAllByDate(
			final Integer year, final Integer month, final Long memberId) {

		final Member member = memberQueryService.findByMemberId(memberId);
		return monthlyGoalRepository.findMonthlyGoalInMonth(year, month, member);
	}
}
