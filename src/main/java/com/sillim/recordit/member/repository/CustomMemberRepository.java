package com.sillim.recordit.member.repository;

import com.sillim.recordit.member.domain.Member;
import java.util.Optional;

public interface CustomMemberRepository {
	Optional<Member> findByAccount(String oauthAccount);
}
