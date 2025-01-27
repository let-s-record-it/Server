package com.sillim.recordit.invite.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.repository.InviteLinkRepository;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

    private final InviteLinkRepository inviteLinkRepository;
    private final CalendarService calendarService;

    public String generateInviteLink(Long calendarId) {
        return Base64.getUrlEncoder().encodeToString(inviteLinkRepository
                .save(
                        new InviteLink(
                                UUID.randomUUID().toString(),
                                LocalDateTime.now().plusDays(7),
                                false,
                                calendarService.searchByCalendarId(calendarId)))
                .getInviteCode().getBytes());
    }
}
