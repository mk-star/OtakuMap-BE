package com.otakumap.global.security.jwt.util;

import com.otakumap.global.security.jwt.dto.JwtDTO;
import com.otakumap.global.security.PrincipalDetailsService;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.util.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    @Value("${spring.jwt.secret}")
    private String secretKeyString;

    private SecretKey secret;

    @Value("${spring.jwt.token.access-expiration-time}")
    private Long accessExpiration;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private Long refreshExpiration;

    private final RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        this.secret = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String createAccessToken(Long userId) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusMillis(accessExpiration);

        return Jwts.builder()
                .setHeader(Map.of("alg", "HS256", "typ", "JWT"))
                .setSubject(String.valueOf(userId))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiredAt))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성 (UUID 이용)
    public String createRefreshToken(Long userId) {
        long expiredAt = System.currentTimeMillis() + refreshExpiration;

        String refreshToken = UUID.randomUUID().toString();

        redisUtil.set("RT::" + refreshToken, "USER::" + userId);
        redisUtil.expire(refreshToken, expiredAt, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    // 헤더에서 토큰 추출
    public String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.split(" ")[1];
    }

    // AccessToken 유효성 확인
    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            return claims.getBody().getExpiration().after(Date.from(Instant.now()));
        } catch (JwtException e) {
            log.error(e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage() + ": 토큰이 유효하지 않습니다.");
            return false;
        }
    }

    // RefreshToken 유효성 확인
    public void validateRefreshToken(JwtDTO request) {
        //redis 확인
        String key = "RT::" + request.getRefreshToken();

        if (!redisUtil.exists(key)) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }

        String value = "USER::" + getId(request.getAccessToken());
        if(value.equals(redisUtil.get(key))) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    //id(PK) 추출
    public Long getId(String token) { return Long.parseLong(getClaims(token).getBody().getSubject()); }

    //토큰의 클레임 가져오는 메서드
    public Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    // 토큰 재발급
    public JwtDTO reissueToken(JwtDTO request) throws SignatureException {
        Long userId = getId(request.getAccessToken());

        // 기존 Refresh Token을 삭제
        redisUtil.delete("RT::" + request.getRefreshToken());

        // 새로운 토큰 발급
        return new JwtDTO(
                createAccessToken(userId),
                createRefreshToken(userId)
        );
    }

    // 토큰 유효시간 반환
    public Long getExpTime(String token) {
        return getClaims(token).getPayload().getExpiration().getTime();
    }
}