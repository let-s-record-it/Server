package com.sillim.recordit.calendar.repository;

import static com.sillim.recordit.calendar.domain.QCalendarMember.calendarMember;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomCalendarMemberRepositoryImpl extends QuerydslRepositorySupport
		implements CustomCalendarMemberRepository {

	public CustomCalendarMemberRepositoryImpl() {
		super(CalendarMember.class);
	}

	@Override
	public Optional<CalendarMember> findCalendarMember(Long calendarId, Long memberId) {
		return Optional.ofNullable(
				selectFrom(calendarMember)
						.leftJoin(calendarMember.calendar)
						.fetchJoin()
						.leftJoin(calendarMember.calendar.category)
						.fetchJoin()
						.leftJoin(calendarMember.member)
						.fetchJoin()
						.where(calendarMember.deleted.isFalse())
						.where(calendarMember.calendar.id.eq(calendarId))
						.where(calendarMember.member.id.eq(memberId))
						.fetchOne());
	}

	@Override
	public List<CalendarMember> findCalendarMembers(Long calendarId) {
		return selectFrom(calendarMember)
				.leftJoin(calendarMember.calendar)
				.fetchJoin()
				.leftJoin(calendarMember.calendar.category)
				.fetchJoin()
				.leftJoin(calendarMember.member)
				.fetchJoin()
				.where(calendarMember.deleted.isFalse())
				.where(calendarMember.calendar.id.eq(calendarId))
				.fetch();
	}

	@Override
	public List<Calendar> findCalendarsByMemberId(Long memberId) {
		return getJpaQueryFactory()
				.select(calendarMember.calendar)
				.from(calendarMember)
				.leftJoin(calendarMember.calendar.category)
				.fetchJoin()
				.where(calendarMember.deleted.isFalse())
				.where(calendarMember.calendar.deleted.isFalse())
				.where(calendarMember.member.id.eq(memberId))
				.fetch();
	}
}
