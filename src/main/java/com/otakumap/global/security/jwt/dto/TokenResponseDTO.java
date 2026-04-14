package com.otakumap.global.security.jwt.dto;

import org.springframework.http.ResponseCookie;

public record TokenResponseDTO(Long id, String accessToken, ResponseCookie cookie) {
}
