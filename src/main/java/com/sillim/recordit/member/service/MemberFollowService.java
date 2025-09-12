package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.service.AlarmService;
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
	private final AlarmService alarmService;

	@Retryable(
			retryFor = {
				OptimisticLockException.class,
				ObjectOptimisticLockingFailureException.class,
				StaleObjectStateException.class
			},
			maxAttempts = 15,
			backoff = @Backoff(delay = 30))
	public void follow(Long followerId, Long followedId) {
		Member follower = memberQueryService.findByMemberId(followerId);
		Member followed = memberQueryService.findByMemberId(followedId);

		if (follower.equalsId(followedId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}

		follower.follow(followed);
		memberRepository.save(follower);
		alarmService.pushAlarm(
				follower.getId(),
				followed.getId(),
				PushMessage.fromFollowing(follower.getId(), follower.getPersonalId()));
	}

	@Retryable(
			retryFor = {
				OptimisticLockException.class,
				ObjectOptimisticLockingFailureException.class,
				StaleObjectStateException.class
			},
			maxAttempts = 15,
			backoff = @Backoff(delay = 30))
	public void unfollow(Long followerId, Long followedId) {
		Member follower = memberQueryService.findByMemberId(followerId);
		Member followed = memberQueryService.findByMemberId(followedId);

		follower.unfollow(followed);
		memberRepository.save(follower);
		memberRepository.save(followed);
	}
}
