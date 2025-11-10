package com.otakumap.global.security.jwt.dto;

import org.springframework.http.ResponseCookie;

public record TokenPair (Long id, String accessToken, ResponseCookie cookie) {
}
