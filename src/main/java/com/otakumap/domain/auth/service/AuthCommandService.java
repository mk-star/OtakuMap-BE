package com.otakumap.domain.auth.service;

import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.AuthResponseDTO;
import com.otakumap.global.security.jwt.dto.RefreshTokenRequestDTO;
import com.otakumap.global.security.jwt.dto.TokenPair;
import com.otakumap.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthCommandService {
    User signup(AuthRequestDTO.SignupDTO request);
    TokenPair login(AuthRequestDTO.LoginDTO request);
    void verifyEmail(AuthRequestDTO.VerifyEmailDTO request);
    boolean verifyCode(AuthRequestDTO.VerifyCodeDTO request);
    TokenPair reissueToken(HttpServletRequest request, RefreshTokenRequestDTO refreshTokenRequest);
    //void logout(TokenSuccessResponseDTO request);
    void findPassword(AuthRequestDTO.FindPasswordDTO request);
    boolean verifyResetCode(AuthRequestDTO.VerifyResetCodeDTO request);
}
