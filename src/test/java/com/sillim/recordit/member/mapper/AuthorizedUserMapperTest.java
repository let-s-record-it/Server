package com.sillim.recordit.member.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthorizedUserMapperTest {

	@Test
	@DisplayName("멤버를 인증된 멤버로 변환한다.")
	void mappingMemberToAuthorizedMember() {
		Member member = MemberFixture.DEFAULT.getMember();
		AuthorizedUserMapper mapper = new AuthorizedUserMapper();

		AuthorizedUser authorizedUser = mapper.toAuthorizedUser(member);

		assertThat(authorizedUser.getMemberId()).isEqualTo(member.getId());
		assertThat(authorizedUser.getAuthorities()).isEqualTo(member.getAuthorities());
	}
}
