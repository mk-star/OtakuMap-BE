package com.otakumap.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otakumap.global.security.PrincipalDetailsService;
import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.global.apiPayload.ApiResponse;
import com.otakumap.global.apiPayload.code.BaseErrorCode;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.GeneralException;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtProvider.getAccessToken(request);

            //JWT 유효성 검증
            if(accessToken != null && jwtProvider.validateAccessToken(accessToken)) {
                String blackListValue = (String) redisUtil.get("AT::" + accessToken);
                if (blackListValue != null && blackListValue.equals("logout")) {
                    throw new AuthHandler(ErrorStatus.TOKEN_LOGGED_OUT);
                }

                Long userId = jwtProvider.getId(accessToken);
                setAuthenticationToContext(userId, request);
            }
            // 다음 필터로 넘기기
            filterChain.doFilter(request, response);
        } catch (GeneralException e) {
            BaseErrorCode code = e.getCode();
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(code.getReasonHttpStatus().getHttpStatus().value());

            ApiResponse<Object> errorResponse = ApiResponse.onFailure(
                    code.getReasonHttpStatus().getCode(),
                    code.getReasonHttpStatus().getMessage(),
                    e.getMessage());

            ObjectMapper om = new ObjectMapper();
            om.writeValue(response.getOutputStream(), errorResponse);
        }
    }

    private void setAuthenticationToContext(Long userId, HttpServletRequest request) {
        UserDetails userDetails = principalDetailsService.loadUserById(userId);
        if (userDetails == null) {
            throw new AuthHandler(ErrorStatus.USER_NOT_FOUND);
        }

        //userDetails, password, role -> 접근 권한 인증 Token 생성
        UsernamePasswordAuthenticationToken  authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());

        // 클라이언트 요청 정보(IP, 세션 등) 저장
        //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //현재 Request의 Security Context에 접근 권한 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}