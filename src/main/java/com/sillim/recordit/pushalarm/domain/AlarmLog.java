package com.sillim.recordit.pushalarm.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.pushalarm.dto.AlarmType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmLog extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_log_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long activeId;

	@Column(nullable = false)
	private AlarmType alarmType;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String body;

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;

	@Column(nullable = false)
	private boolean deleted;

	@Builder
	public AlarmLog(
			Long activeId,
			AlarmType alarmType,
			String title,
			String body,
			Long senderId,
			Long receiverId) {
		this.activeId = activeId;
		this.alarmType = alarmType;
		this.title = title;
		this.body = body;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.deleted = false;
	}

	public boolean isReceiver(Long id) {
		return this.receiverId.equals(id);
	}

	public void delete() {
		this.deleted = true;
	}
}
