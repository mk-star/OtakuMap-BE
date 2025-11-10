package com.otakumap.global.security.service;

import com.otakumap.global.security.jwt.properties.JwtProperties;
import com.otakumap.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist:";

    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

    private String keyFor(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    private String blacklistKeyFor(String accessToken) {
        return ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken;
    }

    /**
     * 리프레시 토큰이 Redis에 있는지 확인합니다.
     * @param userId
     * @param refreshToken
     */
    public void validateRefreshToken(Long userId, String refreshToken) {
        String key = keyFor(userId);

        String savedToken = redisUtil.get(key);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new ExpiredJwtException(null, null, "Refresh token has expired");
        }
    }

    /**
     * 리프레시 토큰을 Redis에 저장합니다.
     * @param userId
     * @param refreshToken
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = keyFor(userId);
        redisUtil.set(key, refreshToken, jwtProperties.refreshExpirationTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * Access Token을 블랙리스트에 추가하고 Refresh Token을 레디스에서 삭제합니다.
     */
    public void logout(Long userId, String accessToken) {
        // Access Token을 블랙 리스트에 추가
        blacklistAccessToken(accessToken);

        // Refresh Token을 레디스에서 삭제
        deleteRefresh(userId);
    }

    /**
     * 리프레시 토큰을 삭제합니다.
     */
    public void deleteRefresh(Long userId) {
        redisUtil.delete(keyFor(userId));
    }

    /**
     * Access Token을 블랙리스트에 추가합니다.
     */
    public void blacklistAccessToken(String accessToken) {
        String blacklistKey = blacklistKeyFor(accessToken);
        // Access Token의 남은 유효시간만큼 블랙리스트에 저장
        redisUtil.set(blacklistKey, "blacklisted", jwtProperties.accessExpirationTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * Access Token이 블랙리스트에 있는지 검증합니다.
     */
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String blacklistKey = blacklistKeyFor(accessToken);
        return redisUtil.exists(blacklistKey);
    }

}