package com.otakumap.global.security.jwt.util;

import com.otakumap.global.security.jwt.properties.JwtProperties;
import com.otakumap.global.security.service.TokenService;
import com.otakumap.global.security.util.CookieUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.otakumap.global.security.util.CookieUtil.REFRESH_TOKEN_COOKIE;

/**
 * JWT 토큰 생성, 검증, 쿠키 관리를 담당하는 유틸리티 클래스
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final JwtProperties jwtProperties;

    /**
     * Access Token을 생성합니다.
     *
     * @param userId 사용자 ID
     * @return JWT Access Token
     */
    public String createAccessToken(Long userId) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusMillis(jwtProperties.accessExpirationTime());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiredAt))
                .signWith(getAccessTokenKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Refresh Token을 생성합니다.
     *
     * @return Refresh Token
     */
    public String createRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Refresh Token 과 token을 담을 쿠키를 생성하고 Redis에 저장합니다.
     *
     * @param userId 사용자 ID
     * @param refreshToken 생성한 Refresh Token
     * @return Refresh Token ResponseCookie
     */
    public ResponseCookie generateRefreshTokenCookie(Long userId, String refreshToken) {
        // Redis에 Refresh Token 저장
        tokenService.saveRefreshToken(userId, refreshToken);

        return cookieUtil.createRefreshTokenCookie(REFRESH_TOKEN_COOKIE, refreshToken);
    }

    /**
     * Access Token의 유효성을 검증합니다.
     * 토큰이 유효하지 않거나 만료된 경우 예외를 발생시킵니다.
     *
     * @param token 검증할 Access Token
     * @throws JwtException 토큰이 유효하지 않은 경우
     * @throws ExpiredJwtException 토큰이 만료된 경우
     */
    public void validateAccessToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Access token is null or empty");
        }

        try {
            Claims claims = getClaims(token);
            if (claims.getSubject() == null || claims.getSubject().trim().isEmpty()) {
                throw new JwtException("Access token subject is missing");
            }

            // 만료됐을 경우
            if (isExpired(claims)) {
                throw new ExpiredJwtException(null, claims, "Access token has expired");
            }
        } catch (ExpiredJwtException e) {
            log.debug("Access token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid access token: {}", e.getMessage());
            throw new JwtException("Invalid access token", e);
        }
    }

    /**
     * Access Token에서 Claims를 추출합니다.
     *
     * @param token Access Token
     * @return JWT Claims
     * @throws JwtException 토큰 파싱 실패 시
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getAccessTokenKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new JwtException("Failed to parse access token claims", e);
        }
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     *
     * @param claims JWT Claims 객체
     * @return 만료된 경우 true, 그렇지 않으면 false
     */
    private boolean isExpired(Claims claims) {
        if (claims == null || claims.getExpiration() == null) {
            return true;
        }
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    /**
     * Access Token에서 사용자 ID를 추출합니다.
     *
     * @param token Access Token
     * @return 사용자 ID
     * @throws JwtException 토큰이 유효하지 않은 경우
     */
    public Long getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Token is null or empty");
        }

        try {
            Claims claims = getClaims(token);

            String userId = claims.getSubject();
            if (userId == null || userId.trim().isEmpty()) {
                throw new JwtException("Token subject is missing");
            }

            return Long.valueOf(userId);
        } catch (JwtException e) {
            log.debug("Failed to extract user ID from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * JWT 생성/검증 시 서명 키를 생성합니다.
     *
     * @return SecretKey 객체
     */
    private SecretKey getAccessTokenKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.accessSecretKey()));
    }

}