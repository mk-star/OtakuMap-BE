package com.otakumap.domain.auth.service;

import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.AuthResponseDTO;
import com.otakumap.global.security.jwt.dto.JwtDTO;
import com.otakumap.domain.user.entity.User;

public interface AuthCommandService {
    User signup(AuthRequestDTO.SignupDTO request);
    AuthResponseDTO.LoginResultDTO login(AuthRequestDTO.LoginDTO request);
    void verifyEmail(AuthRequestDTO.VerifyEmailDTO request);
    boolean verifyCode(AuthRequestDTO.VerifyCodeDTO request);
    JwtDTO reissueToken(JwtDTO request);
    void logout(JwtDTO request);
    void findPassword(AuthRequestDTO.FindPasswordDTO request);
    boolean verifyResetCode(AuthRequestDTO.VerifyResetCodeDTO request);
}
