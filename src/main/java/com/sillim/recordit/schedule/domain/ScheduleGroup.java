package com.sillim.recordit.schedule.domain;

import jakarta.persistence.*;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_group_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Boolean isRepeated;

	@OneToOne(mappedBy = "scheduleGroup")
	private RepetitionPattern repetitionPattern;

	public ScheduleGroup(Boolean isRepeated) {
		this.isRepeated = isRepeated;
	}

	public void setRepetitionPattern(RepetitionPattern repetitionPattern) {
		this.repetitionPattern = repetitionPattern;
	}

	public Optional<RepetitionPattern> getRepetitionPattern() {
		return Optional.ofNullable(repetitionPattern);
	}
}
