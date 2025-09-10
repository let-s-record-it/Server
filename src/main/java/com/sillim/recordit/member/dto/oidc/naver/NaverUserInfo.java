package com.sillim.recordit.member.dto.oidc.naver;

public record NaverUserInfo(String resultcode, String message, NaverProfile response) {}
