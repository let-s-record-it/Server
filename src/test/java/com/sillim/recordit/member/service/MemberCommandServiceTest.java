package com.sillim.recordit.member.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

	@Mock MemberQueryService memberQueryService;
	@InjectMocks MemberCommandService memberCommandService;

	@Test
	@DisplayName("멤버 정보를 수정할 수 있다.")
	void modifyMemberInfo() {
		Member member = mock(Member.class);
		BDDMockito.given(memberQueryService.findByMemberId(eq(1L))).willReturn(member);
		ProfileModifyRequest request = new ProfileModifyRequest("name", "job");

		memberCommandService.modifyMemberInfo(request, 1L);

		then(member).should(times(1)).modifyInfo("name", "job");
	}

	@Test
	@DisplayName("회원 탈퇴 할 수 있다.")
	void withdrawMember() {
		Member member = mock(Member.class);
		BDDMockito.given(memberQueryService.findByMemberId(eq(1L))).willReturn(member);

		memberCommandService.withdrawMember(1L);

		then(member).should(times(1)).delete();
	}
}
