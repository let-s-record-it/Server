package com.sillim.recordit.member.repository;

import com.sillim.recordit.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByAuthOauthAccount(String oauthAccount);
}
