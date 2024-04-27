package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeekdayBit {

    private static final int WEEKDAY_BIT_MIN = 0;
    private static final int WEEKDAY_BIT_MAX = 127;

    @Column
    private Integer weekdayBit;

    public WeekdayBit(Integer weekdayBit) {
        validate(weekdayBit);
        this.weekdayBit = weekdayBit;
    }

    private void validate(Integer weekdayBit) {
        if (weekdayBit < WEEKDAY_BIT_MIN || weekdayBit > WEEKDAY_BIT_MAX) {
            throw new InvalidWeekdayBitException(ErrorCode.WEEKDAY_BIT_OUT_OF_RANGE);
        }
    }

    public boolean isValidWeekday(DayOfWeek dayOfWeek) {
        return (weekdayBit & (1 << (dayOfWeek.getValue() - 1))) > 0;
    }
}
