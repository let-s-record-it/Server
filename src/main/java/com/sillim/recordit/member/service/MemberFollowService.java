package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberFollowService {

	private final MemberQueryService memberQueryService;
	private final MemberRepository memberRepository;

	@Retryable(retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class,
			StaleObjectStateException.class}, maxAttempts = 15, backoff = @Backoff(delay = 30))
	public void follow(Long followerId, Long followedId) {
		Member follower = memberQueryService.findByMemberId(followerId);
		Member followed = memberQueryService.findByMemberId(followedId);

		follower.follow(followed);
		memberRepository.save(follower);
	}

	@Retryable(retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class,
			StaleObjectStateException.class}, maxAttempts = 15, backoff = @Backoff(delay = 30))
	public void unfollow(Long followerId, Long followedId) {
		Member follower = memberQueryService.findByMemberId(followerId);
		Member followed = memberQueryService.findByMemberId(followedId);

		follower.unfollow(followed);
		memberRepository.save(follower);
		memberRepository.save(followed);
		// memberRepository.deleteFollowRelation(followerId, followedId);
	}
}
