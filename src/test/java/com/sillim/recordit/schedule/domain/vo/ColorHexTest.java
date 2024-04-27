package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidColorHexException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ColorHexTest {

    @Test
    @DisplayName("color hex 값에 맞는 ColorHex를 생성할 수 있다.")
    void validIfFitColorHex() {
        ColorHex colorHex1 = new ColorHex("#112233");
        ColorHex colorHex2 = new ColorHex("#ffda12fb");
        ColorHex colorHex3 = new ColorHex("#fab");

        assertAll(() -> {
            assertThat(colorHex1).isEqualTo(new ColorHex("#112233"));
            assertThat(colorHex2).isEqualTo(new ColorHex("#ffda12fb"));
            assertThat(colorHex3).isEqualTo(new ColorHex("#fab"));
            assertThat(colorHex1.getColorHex()).isEqualTo("#112233");
            assertThat(colorHex2.getColorHex()).isEqualTo("#ffda12fb");
            assertThat(colorHex3.getColorHex()).isEqualTo("#fab");
        });
    }

    @Test
    @DisplayName("color hex 값에 맞지 않을 시 InvalidColorHexException이 발생한다.")
    void throwInvalidColorHexExceptionIfNotFitColorHex() {
        assertAll(() -> {
            assertThatCode(() -> new ColorHex("11233"))
                    .isInstanceOf(InvalidColorHexException.class)
                    .hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
            assertThatCode(() -> new ColorHex("#ggg"))
                    .isInstanceOf(InvalidColorHexException.class)
                    .hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
            assertThatCode(() -> new ColorHex("#11233"))
                    .isInstanceOf(InvalidColorHexException.class)
                    .hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
            assertThatCode(() -> new ColorHex("#ffda12f"))
                    .isInstanceOf(InvalidColorHexException.class)
                    .hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
            assertThatCode(() -> new ColorHex("#fb"))
                    .isInstanceOf(InvalidColorHexException.class)
                    .hasMessage(ErrorCode.INVALID_SCHEDULE_COLOR_HEX.getDescription());
        });
    }
}