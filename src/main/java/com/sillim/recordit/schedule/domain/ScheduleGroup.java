package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
    private boolean isRepeated;

    @OneToOne(mappedBy = "scheduleGroup", orphanRemoval = true)
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

    public void modifyRepeated(
            RepetitionType repetitionType,
            Integer repetitionPeriod,
            LocalDateTime repetitionStartDate,
            LocalDateTime repetitionEndDate,
            Integer monthOfYear,
            Integer dayOfMonth,
            WeekNumber weekNumber,
            Weekday weekday,
            Integer weekdayBit) {
        this.repetitionPattern = RepetitionPattern.create(repetitionType, repetitionPeriod,
                repetitionStartDate, repetitionEndDate, monthOfYear, dayOfMonth, weekNumber,
                weekday, weekdayBit, this);
        this.isRepeated = true;
    }

    public void modifyNotRepeated() {
        this.isRepeated = false;
        this.repetitionPattern = null;
    }
}
