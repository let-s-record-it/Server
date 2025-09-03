package com.sillim.recordit.calendar.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.fixture.MemberFixture;

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

    @Qualifier("customCalendarMemberRepositoryImpl")
    @Autowired
    CustomCalendarMemberRepositoryImpl customCalendarMemberRepository;

    @Autowired
    TestEntityManager em;

    Calendar calendar;
    CalendarCategory category;
    Member member;

    @BeforeEach
    void initObjects() {
        member = em.persist(MemberFixture.DEFAULT.getMember());
        category = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
        calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(member, category));
    }

    @Test
    @DisplayName("캘린더 멤버를 조회한다.")
    void findCalendarMember() {
        em.persist(new CalendarMember(member, calendar));

        Optional<CalendarMember> foundCalendarMember =
                customCalendarMemberRepository.findCalendarMember(calendar.getId(), member.getId());

        assertAll(
                () -> {
                    assertThat(foundCalendarMember).isNotEmpty();
                    assertThat(foundCalendarMember.get().getCalendar().getId())
                            .isEqualTo(calendar.getId());
                    assertThat(foundCalendarMember.get().getMember().getId())
                            .isEqualTo(member.getId());
                });
    }

    @Test
    @DisplayName("캘린더 멤버들을 조회한다.")
    void findCalendarMembers() {
        Member member2 =
                em.persist(
                        new Member(
                                new Auth("account", OAuthProvider.KAKAO),
                                "name",
                                "job",
                                "email",
                                "image",
                                0L,
                                0L,
                                false,
                                true,
                                List.of()));
        em.persist(new CalendarMember(member, calendar));
        em.persist(new CalendarMember(member2, calendar));

        List<CalendarMember> calendarMembers =
                customCalendarMemberRepository.findCalendarMembers(calendar.getId());

        assertThat(calendarMembers).hasSize(2);
    }

    @Test
    @DisplayName("특정 멤버의 캘린더들을 조회한다.")
    void findCalendarsByMemberId() {
        CalendarCategory category =
                em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
        Calendar calendar2 = em.persist(CalendarFixture.DEFAULT.getCalendar(member, category));
        em.persist(new CalendarMember(member, calendar));
        em.persist(new CalendarMember(member, calendar2));

        List<Calendar> calendars =
                customCalendarMemberRepository.findCalendarsByMemberId(member.getId());

        assertThat(calendars).hasSize(2);
    }
}
