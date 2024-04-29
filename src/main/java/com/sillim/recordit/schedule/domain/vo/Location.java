package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidLocationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

	private static final int LATITUDE_MIN = 0;
	private static final int LONGITUDE_MIN = 0;
	private static final int LATITUDE_MAX = 90;
	private static final int LONGITUDE_MAX = 180;

	@Column private double latitude;

	@Column private double longitude;

	private Location(double latitude, double longitude) {
		validate(latitude, longitude);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private static void validate(double latitude, double longitude) {
		if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX) {
			throw new InvalidLocationException(ErrorCode.LATITUDE_OUT_OF_RANGE);
		}

		if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
			throw new InvalidLocationException(ErrorCode.LONGITUDE_OUT_OF_RANGE);
		}
	}
}
