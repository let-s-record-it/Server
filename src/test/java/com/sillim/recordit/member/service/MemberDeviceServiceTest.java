package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberDeviceRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberDeviceServiceTest {

	@Mock
	MemberDeviceRepository memberDeviceRepository;
	@InjectMocks
	MemberDeviceService memberDeviceService;

	@Test
	@DisplayName("디바이스가 존재하지 않으면 추가할 수 있다.")
	void addedMemberDeviceIfNotExists() {
		given(memberDeviceRepository.existsByIdentifierAndMemberId(eq("id"), any())).willReturn(false);

		memberDeviceService.addMemberDeviceIfNotExists("id", "model", "token", MemberFixture.DEFAULT.getMember());

		then(memberDeviceRepository).should(times(1)).existsByIdentifierAndMemberId(eq("id"), any());
	}

	@Test
	@DisplayName("디바이스가 존재하면 추가되지 않는다.")
	void notAddedMemberDeviceIfExists() {
		given(memberDeviceRepository.existsByIdentifierAndMemberId(eq("id"), any())).willReturn(true);

		memberDeviceService.addMemberDeviceIfNotExists("id", "model", "token", MemberFixture.DEFAULT.getMember());

		then(memberDeviceRepository).should(times(0)).save(any());
	}

	@Test
	@DisplayName("특정 멤버의 fcm token들을 조회할 수 있다.")
	void searchFcmTokensByMember() {
		long memberId = 1L;
		List<String> tokens = List.of("token1", "token2");
		given(memberDeviceRepository.findFcmTokensByMemberId(eq(memberId))).willReturn(tokens);

		List<String> fcmTokens = memberDeviceService.searchFcmTokensByMemberId(memberId);

		assertThat(fcmTokens).containsExactlyElementsOf(tokens);
	}
}
