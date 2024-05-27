package com.sillim.recordit.member.service;

import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

	private final MemberRepository memberRepository;
	private final CalendarService calendarService;

	@Transactional
	public Member signup(MemberInfo memberInfo) {
		Member member = memberRepository.save(memberInfo.toMember());
		calendarService.addCalendar(CalendarAddRequest.createGeneralCalendar(), member.getId());
		return member;
	}
}
