package com.sillim.recordit.member.service;

import com.sillim.recordit.calendar.repository.CalendarCategoryRepository;
import com.sillim.recordit.calendar.repository.CalendarMemberRepository;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
import com.sillim.recordit.feed.repository.FeedLikeRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.feed.repository.FeedScrapRepository;
import com.sillim.recordit.member.repository.MemberDeviceRepository;
import com.sillim.recordit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberDeleteService {

	private final MemberDeviceRepository memberDeviceRepository;
	private final CalendarCategoryRepository calendarCategoryRepository;
	private final ScheduleCategoryRepository scheduleCategoryRepository;
	private final CalendarRepository calendarRepository;
	private final CalendarMemberRepository calendarMemberRepository;
	private final FeedRepository feedRepository;
	private final FeedCommentRepository feedCommentRepository;
	private final FeedLikeRepository feedLikeRepository;
	private final FeedScrapRepository feedScrapRepository;
	private final MemberRepository memberRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void hardDeleteMember(Long memberId) {
		calendarCategoryRepository.updateMemberIsNull(memberId);
		scheduleCategoryRepository.updateMemberIsNull(memberId);
		calendarRepository.updateMemberIsNull(memberId);
		calendarMemberRepository.updateMemberIsNull(memberId);
		feedRepository.updateMemberIsNull(memberId);
		feedCommentRepository.updateMemberIsNull(memberId);
		feedLikeRepository.updateMemberIsNull(memberId);
		feedScrapRepository.updateMemberIsNull(memberId);
		memberDeviceRepository.deleteByMemberId(memberId);

		memberRepository.deleteById(memberId);
	}
}
