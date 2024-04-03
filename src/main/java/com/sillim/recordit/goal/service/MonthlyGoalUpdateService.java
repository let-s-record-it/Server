package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalAddRequest;
import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyGoalUpdateService {

	private final MonthlyGoalJpaRepository monthlyGoalJpaRepository;

	public void add(MonthlyGoalAddRequest request, Long memberId) {
		Member member = new Member(); // TODO Member Entity 구현 완료 시 변경
		monthlyGoalJpaRepository.save(request.toEntity(member));
	}
}
