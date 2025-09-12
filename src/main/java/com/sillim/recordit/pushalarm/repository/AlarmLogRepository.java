package com.sillim.recordit.pushalarm.repository;

import com.sillim.recordit.pushalarm.domain.AlarmLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {

	Slice<AlarmLog> findByDeletedIsFalseAndReceiverIdOrderByCreatedAtDesc(
			Pageable pageable, Long receiverId);
}
