package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {

	List<CalendarCategory> findByDeletedIsFalseAndMemberId(Long memberId);

	CalendarCategory findByDeletedIsFalseAndMemberIdAndIsDefaultIsTrue(Long memberId);

	@Modifying(clearAutomatically = true)
	@Query(
			"update CalendarCategory cc set cc.memberId = null, cc.deleted = true where cc.memberId"
					+ " = :memberId")
	void updateMemberIsNull(@Param("memberId") Long memberId);
}
