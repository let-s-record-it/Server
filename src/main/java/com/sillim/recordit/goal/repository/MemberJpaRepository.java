package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}
