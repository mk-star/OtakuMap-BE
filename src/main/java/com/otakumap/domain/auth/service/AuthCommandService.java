package com.otakumap.domain.auth.service;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.SignUpRequestDTO;
import com.otakumap.global.security.jwt.dto.LoginRequestDTO;
import com.otakumap.global.security.jwt.dto.RefreshTokenRequestDTO;
import com.otakumap.global.security.jwt.dto.TokenResponseDTO;
import com.otakumap.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthCommandService {
    User signup(SignUpRequestDTO request);
    TokenResponseDTO login(LoginRequestDTO request);
    void verifyEmail(AuthRequestDTO.VerifyEmailDTO request);
    void verifyCode(AuthRequestDTO.VerifyCodeDTO request);
    TokenResponseDTO reissueToken(HttpServletRequest request, RefreshTokenRequestDTO refreshTokenRequest);
    void findPassword(AuthRequestDTO.FindPasswordDTO request);
    void verifyResetCode(AuthRequestDTO.VerifyResetCodeDTO request);
}
