package com.sillim.recordit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleCategoryQueryServiceTest {

	@Mock ScheduleCategoryRepository scheduleCategoryRepository;
	@InjectMocks ScheduleCategoryQueryService scheduleCategoryQueryService;

	@Test
	@DisplayName("특정 멤버의 캘린더 카테고리들을 조회할 수 있다.")
	void searchCalendarCategoriesByMemberId() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
		given(scheduleCategoryRepository.findByDeletedIsFalseAndMemberId(eq(memberId)))
				.willReturn(List.of(category));

		List<ScheduleCategory> categories =
				scheduleCategoryQueryService.searchScheduleCategories(memberId);

		assertThat(categories.get(0)).isEqualTo(category);
	}

	@Test
	@DisplayName("특정 캘린더 카테고리를 조회할 수 있다.")
	void searchCalendarCategory() {
		long categoryId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
		given(scheduleCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		ScheduleCategory foundCategory =
				scheduleCategoryQueryService.searchScheduleCategory(categoryId);

		assertThat(foundCategory).isEqualTo(category);
	}
}
