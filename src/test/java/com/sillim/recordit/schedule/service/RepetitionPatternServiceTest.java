package com.sillim.recordit.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.RepetitionUpdateRequest;
import com.sillim.recordit.schedule.repository.RepetitionPatternRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepetitionPatternServiceTest {

	@Mock RepetitionPatternRepository repetitionPatternRepository;
	@InjectMocks RepetitionPatternService repetitionPatternService;

	Member member;

	@BeforeEach
	void initObjects() {
		member =
				Member.builder()
						.auth(new Auth("1234567", OAuthProvider.KAKAO))
						.name("name")
						.job("job")
						.deleted(false)
						.memberRole(List.of(MemberRole.ROLE_USER))
						.build();
	}

	@Test
	@DisplayName("스케줄 반복 패턴을 생성할 수 있다.")
	void addRepetitionPattern() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern expectedRepetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		given(repetitionPatternRepository.save(any(RepetitionPattern.class)))
				.willReturn(expectedRepetitionPattern);

		RepetitionPattern repetitionPattern =
				repetitionPatternService.addRepetitionPattern(
						repetitionUpdateRequest, scheduleGroup);

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionStartDate);
					assertThat(repetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionEndDate);
					assertThat(repetitionPattern.getRepetitionPeriod()).isEqualTo(1);
				});
	}

	@Test
	@DisplayName("스케줄 반복 패턴 갱신 시 이미 반복이 존재하면 수정한다.")
	void modifyRepetitionPatternIfExistsRepetitionPattern() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		ScheduleGroup scheduleGroup = new ScheduleGroup(true);
		RepetitionPattern expectedRepetitionPattern =
				RepetitionPattern.createDaily(
						1,
						LocalDateTime.of(2023, 1, 1, 0, 0),
						LocalDateTime.of(2023, 2, 1, 0, 0),
						scheduleGroup);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.WEEKLY,
						2,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						1);
		scheduleGroup.setRepetitionPattern(expectedRepetitionPattern);

		RepetitionPattern repetitionPattern =
				repetitionPatternService.updateRepetitionPattern(
						repetitionUpdateRequest, scheduleGroup);

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.WEEKLY);
					assertThat(repetitionPattern.getRepetitionPeriod()).isEqualTo(2);
					assertThat(repetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionStartDate);
					assertThat(repetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionEndDate);
					assertThat(repetitionPattern.getWeekdayBit()).isNotEmpty();
				});
	}

	@Test
	@DisplayName("스케줄 반복 패턴 갱신 시 반복이 존재하지 않으면 생성한다.")
	void addRepetitionPatternIfNotExistsRepetitionPattern() {
		LocalDateTime repetitionStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime repetitionEndDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		ScheduleGroup scheduleGroup = new ScheduleGroup(false);
		RepetitionPattern expectedRepetitionPattern =
				RepetitionPattern.createDaily(
						1, repetitionStartDate, repetitionEndDate, scheduleGroup);
		RepetitionUpdateRequest repetitionUpdateRequest =
				new RepetitionUpdateRequest(
						RepetitionType.DAILY,
						1,
						repetitionStartDate,
						repetitionEndDate,
						null,
						null,
						null,
						null,
						null);
		given(repetitionPatternRepository.save(any(RepetitionPattern.class)))
				.willReturn(expectedRepetitionPattern);

		RepetitionPattern repetitionPattern =
				repetitionPatternService.updateRepetitionPattern(
						repetitionUpdateRequest, scheduleGroup);

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.DAILY);
					assertThat(repetitionPattern.getRepetitionPeriod()).isEqualTo(1);
					assertThat(repetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionStartDate);
					assertThat(repetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionEndDate);
				});
	}
}
