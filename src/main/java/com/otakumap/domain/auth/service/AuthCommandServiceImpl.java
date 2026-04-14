package com.otakumap.domain.auth.service;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.SignUpRequestDTO;
import com.otakumap.domain.auth.enums.AuthEmailType;
import com.otakumap.global.security.jwt.dto.LoginRequestDTO;
import com.otakumap.global.security.jwt.dto.RefreshTokenRequestDTO;
import com.otakumap.global.security.jwt.dto.TokenResponseDTO;
import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.security.service.TokenService;
import com.otakumap.global.security.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.otakumap.global.security.util.CookieUtil.REFRESH_TOKEN_COOKIE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMailService mailService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;
    private final AuthCodeGenerator authCodeGenerator;

    @Override
    public User signup(SignUpRequestDTO request) {
        // 이미 존재하는 아이디인 경우 에러
        boolean isExist = userRepository.existsByUserId(request.userId());
        if(isExist) {
            throw new AuthHandler(ErrorStatus.USERID_ALREADY_EXISTS);
        }

        // 입력한 비밀번호, 비밀번호 확인을 검증
        if(!request.password().equals(request.passwordCheck())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }

        // 유저 생성 후 DB에 저장
        User newUser = UserConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.password()));

        return userRepository.save(newUser);
    }

    @Override
    public TokenResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUserId(request.userId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }

        // 로그인 성공 시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken();

        // 리프레시 토큰에 대한 쿠키 생성
        ResponseCookie cookie = jwtProvider.generateRefreshTokenCookie(user.getId(), refreshToken);

        return new TokenResponseDTO(user.getId(), accessToken, cookie);
    }

    @Override
    public void verifyEmail(AuthRequestDTO.VerifyEmailDTO request) {
        String code = authCodeGenerator.generateCode();
        mailService.sendEmail(request.getEmail(), code, AuthEmailType.SIGNUP);
    }

    @Override
    public void verifyCode(AuthRequestDTO.VerifyCodeDTO request) {
        mailService.verifyAuthCode(request.getEmail(), request.getCode(), AuthEmailType.SIGNUP);
    }

    @Override
    public void findPassword(AuthRequestDTO.FindPasswordDTO request) {
        User user = userRepository.findByNameAndUserId(request.getName(), request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));
        String code = authCodeGenerator.generateCode();
        mailService.sendEmail(user.getEmail(), code, AuthEmailType.FIND_PASSWORD);
    }

    @Override
    public void verifyResetCode(AuthRequestDTO.VerifyResetCodeDTO request) {
        User user = userRepository.findByUserId(request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));
        mailService.verifyAuthCode(user.getEmail(), request.getCode(), AuthEmailType.FIND_PASSWORD);
    }

    @Override
    public TokenResponseDTO reissueToken(HttpServletRequest request, RefreshTokenRequestDTO refreshTokenRequest) {
        try {

            // 쿠키에서 리프레시 토큰을 추출
            String refreshToken = CookieUtil.getCookieValue(request, REFRESH_TOKEN_COOKIE);
            if(refreshToken == null) throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);

            // Refresh Token 유효성 검증
            tokenService.validateRefreshToken(refreshTokenRequest.getId(), refreshToken);

            // Refresh Token 삭제
            tokenService.deleteRefresh(refreshTokenRequest.getId());

            // 새로운 token 생성 (RTR 전략으로 access, refresh 둘 다 갱신)
            String newAccessToken = jwtProvider.createAccessToken(refreshTokenRequest.getId());
            String newRefreshToken = jwtProvider.createRefreshToken();

            // 새로운 Refresh Token + 쿠키 생성
            ResponseCookie newCookie = jwtProvider.generateRefreshTokenCookie(refreshTokenRequest.getId(), newRefreshToken);
            return new TokenResponseDTO(refreshTokenRequest.getId(), newAccessToken, newCookie);

        } catch (ExpiredJwtException e) {
            log.debug("Refresh token has expired: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        }
    }

}
