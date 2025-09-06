package com.sillim.recordit.member.repository;

import com.sillim.recordit.config.neo4j.Neo4jRepo;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import java.util.Optional;

import com.sillim.recordit.member.dto.response.FollowRecommendResponse;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

@Neo4jRepo
public interface MemberRepository extends Neo4jRepository<Member, Long> {

	@Query("MATCH (m:Member) WHERE m.oauthAccount = $oauthAccount RETURN m")
	Optional<Member> findByOauthAccount(@Param("oauthAccount") String oauthAccount);

	List<Member> findByPersonalIdStartingWith(String prefix);

	@Query(
			"""
			MATCH (a:Member {personalId: $me})-[r:FOLLOWS]->(b:Member {personalId: $other})
			RETURN COUNT(r) > 0 AS isFollowing
			""")
	boolean existsByFollower(
			@Param("me") String myPersonalId, @Param("other") String otherPersonalId);

	@Query(
			"""
			MATCH (a:Member)-[r:FOLLOWS]->(b:Member)
			WHERE ID(a) = $follower AND ID(b) = $followed
			DELETE r
			""")
	void deleteFollowRelation(
			@Param("follower") Long followerId, @Param("followed") Long followedId);

	@Query("""
			MATCH (n:Member {personalId: $personalId})-[r1:FOLLOWS]->(f1:Member)-[r2:FOLLOWS]->(fx:Member)
			WHERE NOT ((n)-[:FOLLOWS]->(fx)) AND n <> fx
			WITH n, fx, count(fx) as targetCount, collect(distinct f1) as f1List
			OPTIONAL MATCH (n)-[:FOLLOWS]->(c:Member)<-[:FOLLOWS]-(fx)
			WITH fx, targetCount, f1List, count(c) as commonFollowCount
			RETURN ID(fx) as internalId,
			 	   fx.personalId as personalId,
			 	   fx.name as name,
			 	   fx.profileImageUrl as profileImageUrl,
			 	   f1List[0].personalId as weightRepPersonalId,
			 	   f1List[0].name as weightRepName,
			 	   size(f1List) as weightCount,
			 	   commonFollowCount
			ORDER BY targetCount DESC
			""")
	List<FollowRecommendResponse> recommendFriendOfFriends(@Param("personalId") String personalId);
}
