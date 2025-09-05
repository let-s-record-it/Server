package com.sillim.recordit.member.repository;

import com.sillim.recordit.config.neo4j.Neo4jRepo;
import com.sillim.recordit.member.domain.Member;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

@Neo4jRepo
public interface MemberRepository extends Neo4jRepository<Member, Long> {

	@Query("MATCH (m:Member) WHERE m.oauthAccount = $oauthAccount RETURN m")
	Optional<Member> findByOauthAccount(@Param("oauthAccount") String oauthAccount);
}
