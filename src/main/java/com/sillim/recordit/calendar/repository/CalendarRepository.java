package com.sillim.recordit.calendar.repository;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

	List<Calendar> findByMemberId(Long memberId);

	Optional<Calendar> findByIdAndMember(Long calendarId, Member member);
}
