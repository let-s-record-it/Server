package com.sillim.recordit.member.service;

import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.service.CalendarCategoryCommandService;
import com.sillim.recordit.calendar.service.CalendarCommandService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

	private final MemberRepository memberRepository;
	private final CalendarCommandService calendarCommandService;
	private final CalendarCategoryCommandService calendarCategoryCommandService;

	@Transactional
	public Member signup(MemberInfo memberInfo) {
		Member member = memberRepository.save(memberInfo.toMember());
		List<Long> categoryIds = calendarCategoryCommandService.addDefaultCategories(member.getId());
		calendarCommandService.addCalendar(CalendarAddRequest.createGeneralCalendar(categoryIds.get(0)),
				member.getId());
		return member;
	}
}
