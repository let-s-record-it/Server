package com.sillim.recordit.calendar.repository.custom;

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
						.where(calendar.deleted.isFalse(), calendar.id.eq(calendarId))
						.fetchOne());
	}

	@Override
	public List<Calendar> findByMemberId(Long memberId) {
		return selectFrom(calendar)
				.leftJoin(calendar.category)
				.fetchJoin()
				.where(calendar.deleted.isFalse(), calendar.memberId.eq(memberId))
				.fetch();
	}

	@Override
	public void updateCategorySetDefault(Long defaultCategoryId, Long categoryId) {
		update(calendar)
				.set(calendar.category.id, defaultCategoryId)
				.where(calendar.deleted.isFalse(), calendar.category.id.eq(categoryId))
				.execute();
	}
}
