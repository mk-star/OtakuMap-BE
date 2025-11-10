package com.otakumap.global.security.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String accessSecretKey,
        Long accessExpirationTime,
        Long refreshExpirationTime
) {
}