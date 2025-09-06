package com.sillim.recordit.member.repository;

// import static com.sillim.recordit.member.domain.QMember.member;

// public class CustomMemberRepositoryImpl extends QuerydslRepositorySupport
//		implements CustomMemberRepository {
//
//	public CustomMemberRepositoryImpl() {
//		super(Member.class);
//	}
//
//	@Override
//	public Optional<Member> findByAccount(String oauthAccount) {
//		return Optional.ofNullable(
//				selectFrom(member).where(member.auth.oauthAccount.eq(oauthAccount)).fetchOne());
//	}
// }
