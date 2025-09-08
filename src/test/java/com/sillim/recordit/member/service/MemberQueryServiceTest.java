package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

	@Mock
	MemberRepository memberRepository;
	@InjectMocks
	MemberQueryService memberQueryService;

	@Test
	@DisplayName("멤버 ID를 통해 멤버를 조회한다.")
	void findMemberById() {
		Member target = MemberFixture.DEFAULT.getMember();
		Long id = target.getId();
		given(memberRepository.findById(id)).willReturn(Optional.of(target));

		Member member = memberQueryService.findByMemberId(id);

		assertThat(member.getId()).isEqualTo(id);
	}

	@Test
	@DisplayName("멤버가 존재하지 않으면 RecordNotFoundException이 발생한다.")
	void throwRecordNotFoundExceptionIfMemberNotExists() {
		Member target = MemberFixture.DEFAULT.getMember();
		Long id = target.getId();
		given(memberRepository.findById(id)).willReturn(Optional.empty());

		assertThatThrownBy(() -> memberQueryService.findByMemberId(id)).isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.MEMBER_NOT_FOUND.getDescription());
	}
}
