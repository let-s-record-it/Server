package com.sillim.recordit.calendar.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class CustomCalendarMemberRepositoryImplTest {

	@Qualifier("customCalendarMemberRepositoryImpl") @Autowired
	CustomCalendarMemberRepositoryImpl customCalendarMemberRepository;

	@Autowired
	TestEntityManager em;

	Calendar calendar;
	CalendarCategory category;
	long memberId = 1L;

	@BeforeEach
	void initObjects() {
		category = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId));
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(category, memberId));
	}

	@Test
	@DisplayName("캘린더 멤버를 조회한다.")
	void findCalendarMember() {
		em.persist(new CalendarMember(calendar, memberId));

		Optional<CalendarMember> foundCalendarMember = customCalendarMemberRepository
				.findCalendarMember(calendar.getId(), memberId);

		assertAll(() -> {
			assertThat(foundCalendarMember).isNotEmpty();
			assertThat(foundCalendarMember.get().getCalendar().getId()).isEqualTo(calendar.getId());
			assertThat(foundCalendarMember.get().getMemberId()).isEqualTo(memberId);
		});
	}

	@Test
	@DisplayName("캘린더 멤버들을 조회한다.")
	void findCalendarMembers() {
		long member2Id = 2L;
		em.persist(new CalendarMember(calendar, memberId));
		em.persist(new CalendarMember(calendar, member2Id));

		List<CalendarMember> calendarMembers = customCalendarMemberRepository.findCalendarMembers(calendar.getId());

		assertThat(calendarMembers).hasSize(2);
	}

	@Test
	@DisplayName("특정 멤버의 캘린더들을 조회한다.")
	void findCalendarsByMemberId() {
		CalendarCategory category = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId));
		Calendar calendar2 = em.persist(CalendarFixture.DEFAULT.getCalendar(category, memberId));
		em.persist(new CalendarMember(calendar, memberId));
		em.persist(new CalendarMember(calendar2, memberId));

		List<Calendar> calendars = customCalendarMemberRepository.findCalendarsByMemberId(memberId);

		assertThat(calendars).hasSize(2);
	}
}
