package com.sillim.recordit.invite.repository;

import com.sillim.recordit.invite.domain.InviteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteLogRepository extends JpaRepository<InviteLog, Long> {}
