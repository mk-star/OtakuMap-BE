package com.otakumap.global.security.util;

import com.otakumap.global.security.jwt.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Refresh Token을 저장할 쿠키를 생성, 조회, 무효화하는 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class CookieUtil {
    
    private static final long MILLISECONDS_PER_SECOND = 1_000L;
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";
    private static final String COOKIE_PATH = "/";
    private static final String COOKIE_SAME_SITE = "Lax";
    
    private final JwtProperties jwtProperties;
    
    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * HttpServletRequest 에서 이름이 cookieName 인 쿠키 값을 꺼내 반환합니다.
     * 없으면 null
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키를 생성하는 ResponseCookie를 생성합니다.
     *
     * @param cookieName 쿠키 이름
     * @return 생성된 ResponseCookie
     */
    public ResponseCookie createRefreshTokenCookie(String cookieName, String refreshToken) {
        long maxAgeInSeconds = (System.currentTimeMillis() + jwtProperties.refreshExpirationTime()) / MILLISECONDS_PER_SECOND;

        return ResponseCookie.from(cookieName, refreshToken)
                .path(COOKIE_PATH)
                .httpOnly(true)
                .secure(cookieSecure)
                .maxAge(maxAgeInSeconds)
                .sameSite(COOKIE_SAME_SITE)
                .build();
    }

    /**
     * 쿠키를 무효화하는 ResponseCookie를 생성합니다.
     *
     * @param cookieName 무효화할 쿠키 이름
     * @return 무효화된 ResponseCookie
     */
    public ResponseCookie createInvalidatedCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .path(COOKIE_PATH)
                .httpOnly(true)
                .secure(cookieSecure) // YAML 설정값 사용
                .maxAge(0) // 즉시 만료
                .sameSite(COOKIE_SAME_SITE)
                .build();
    }

}