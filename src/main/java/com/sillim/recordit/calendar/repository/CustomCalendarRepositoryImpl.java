package com.sillim.recordit.calendar.repository;

import static com.sillim.recordit.calendar.domain.QCalendar.calendar;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import java.util.List;
import java.util.Optional;

public class CustomCalendarRepositoryImpl extends QuerydslRepositorySupport
		implements CustomCalendarRepository {

	public CustomCalendarRepositoryImpl() {
		super(Calendar.class);
	}

	@Override
	public Optional<Calendar> findByIdWithFetchCategory(Long calendarId) {
		return Optional.ofNullable(
				selectFrom(calendar)
						.leftJoin(calendar.category)
						.fetchJoin()
						.where(calendar.id.eq(calendarId))
						.fetchOne());
	}

	@Override
	public List<Calendar> findByMemberId(Long memberId) {
		return selectFrom(calendar)
				.leftJoin(calendar.category)
				.fetchJoin()
				.where(calendar.member.id.eq(memberId))
				.fetch();
	}
}
