package com.otakumap.global.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otakumap.domain.auth.dto.AuthResponseDTO;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.global.security.PrincipalDetails;
import com.otakumap.global.security.jwt.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

        Long userId = Long.parseLong(oAuth2User.getName());
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        AuthResponseDTO.LoginResultDTO dto = UserConverter.toLoginResultDTO(userId, accessToken, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), dto);

        log.info("소셜 로그인에 성공하였습니다. [User: {}]", userId);
    }
}
