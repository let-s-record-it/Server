package com.sillim.recordit.member.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record KakaoIdTokenPayload(String iss,
                                  String aud,
                                  Long exp,
                                  String sub) {

    public LocalDateTime getLocalDatetimeExp() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault());
    }
}
