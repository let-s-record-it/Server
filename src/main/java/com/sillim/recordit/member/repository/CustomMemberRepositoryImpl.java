package com.sillim.recordit.member.repository;

import static com.sillim.recordit.member.domain.QMember.member;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.member.domain.Member;
import java.util.Optional;

public class CustomMemberRepositoryImpl extends QuerydslRepositorySupport
		implements CustomMemberRepository {

	public CustomMemberRepositoryImpl() {
		super(Member.class);
	}

	@Override
	public Optional<Member> findByAccount(String oauthAccount) {
		return Optional.ofNullable(
				selectFrom(member).where(member.auth.oauthAccount.eq(oauthAccount)).fetchOne());
	}
}
