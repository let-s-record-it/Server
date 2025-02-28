package com.sillim.recordit.category.repository;

import com.sillim.recordit.category.domain.ScheduleCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {
	List<ScheduleCategory> findByDeletedIsFalseAndMemberId(Long memberId);

	ScheduleCategory findByDeletedIsFalseAndMemberIdAndIsDefaultIsTrue(Long memberId);

	@Modifying(clearAutomatically = true)
	@Query(
			"update ScheduleCategory sc set sc.member = null, sc.deleted = true where sc.member.id"
					+ " = :memberId")
	void updateMemberIsNull(@Param("memberId") Long memberId);
}
