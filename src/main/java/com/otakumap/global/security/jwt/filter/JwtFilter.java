package com.otakumap.global.security.jwt.filter;

import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.global.security.service.AuthContextService;
import com.otakumap.global.security.service.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.otakumap.global.security.util.TokenUtil.extractTokenFromHeader;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthContextService authContextService;
    private final TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        if (isSystemUri(uri)) {
            return true;
        }
        return super.shouldNotFilter(request);
    }

    /**
     * 시스템 관련 URI 체크 (OAuth2, Swagger, 에러 페이지 등)
     * 이러한 URI들은 Spring Security 또는 시스템에서 자동으로 처리되는 경로들
     */
    private boolean isSystemUri(String uri) {
        return uri.startsWith("/oauth2/authorization")
                || uri.startsWith("/oauth2/authorize")
                || uri.startsWith("/login/oauth2/code")
                || uri.startsWith("/api/auth")
                || uri.equals("/")
                || uri.startsWith("/error")
                || uri.startsWith("/swagger")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                ;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // Authorization 헤더에서 Access Token 추출
            String accessToken = extractTokenFromHeader(request);

            //JWT 유효성 검증
            jwtProvider.validateAccessToken(accessToken);

            // 블랙리스트 확인
            if (tokenService.isAccessTokenBlacklisted(accessToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            authContextService.createAuthContext(accessToken);
            // 다음 필터로 넘기기
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

}