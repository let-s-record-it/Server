package com.sillim.recordit.invite.repository;

import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.repository.custom.CustomInviteLinkRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteLinkRepository
		extends JpaRepository<InviteLink, Long>, CustomInviteLinkRepository {

	Optional<InviteLink> findByDeletedIsFalseAndCalendarIdAndExpiredIsFalse(Long calendarId);
}
