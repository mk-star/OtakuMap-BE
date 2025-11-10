package com.otakumap.domain.auth.service;

import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.global.security.jwt.dto.RefreshTokenRequestDTO;
import com.otakumap.global.security.jwt.dto.TokenPair;
import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.security.service.TokenService;
import com.otakumap.global.security.util.CookieUtil;
import com.otakumap.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.otakumap.global.security.util.CookieUtil.REFRESH_TOKEN_COOKIE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final MailService mailService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

    @Override
    public User signup(AuthRequestDTO.SignupDTO request) {
        // 이미 존재하는 아이디인 경우 에러
        boolean isExist = userRepository.existsByUserId(request.getUserId());
        if(isExist) {
            throw new AuthHandler(ErrorStatus.USERID_ALREADY_EXISTS);
        }

        // 이미 존재하는 비밀번호인 경우 에러
        if(!request.getPassword().equals(request.getPasswordCheck())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }

        // 유저 생성 후 DB에 저장
        User newUser = UserConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }

    @Override
    public TokenPair login(AuthRequestDTO.LoginDTO request) {
        User user = userRepository.findByUserId(request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }

        Long userId = user.getId();

        // 로그인 성공 시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken();

        // 리프레시 토큰에 대한 쿠키 생성
        ResponseCookie cookie = jwtProvider.generateRefreshTokenCookie(userId, refreshToken);

        return new TokenPair(userId, accessToken, cookie);
    }

    @Override
    public void verifyEmail(AuthRequestDTO.VerifyEmailDTO request) {
        try {
            mailService.sendEmail(request.getEmail(), "signup");
        } catch (MessagingException e) {
            throw new AuthHandler(ErrorStatus.EMAIL_WRITE_FAILED);
        } catch (MailException e) {
            throw new AuthHandler(ErrorStatus.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public boolean verifyCode(AuthRequestDTO.VerifyCodeDTO request) {
        String authCode = redisUtil.get("auth:" + request.getEmail() + ":" + "signup");
        if (authCode == null) {
            throw new AuthHandler(ErrorStatus.EMAIL_CODE_EXPIRED);
        }
        if (!authCode.equals(request.getCode())) {
            throw new AuthHandler(ErrorStatus.CODE_NOT_EQUAL);
        }
        return true;
    }

    @Override
    public void findPassword(AuthRequestDTO.FindPasswordDTO request) {
        User user = userRepository.findByNameAndUserId(request.getName(), request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));
        try {
            mailService.sendEmail(user.getEmail(), "findPassword");
        } catch (MessagingException e) {
            throw new AuthHandler(ErrorStatus.EMAIL_WRITE_FAILED);
        } catch (MailException e) {
            throw new AuthHandler(ErrorStatus.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public boolean verifyResetCode(AuthRequestDTO.VerifyResetCodeDTO request) {
        User user = userRepository.findByUserId(request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));
        String authCode = redisUtil.get("auth:" + user.getEmail() + ":" + "findPassword");
        if (authCode == null) {
            throw new AuthHandler(ErrorStatus.EMAIL_CODE_EXPIRED);
        }
        if (!authCode.equals(request.getCode())) {
            throw new AuthHandler(ErrorStatus.CODE_NOT_EQUAL);
        }
        return true;
    }

    @Override
    public TokenPair reissueToken(HttpServletRequest request, RefreshTokenRequestDTO refreshTokenRequest) {
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
            return new TokenPair(refreshTokenRequest.getId(), newAccessToken, newCookie);

        } catch (ExpiredJwtException e) {
            log.debug("Refresh token has expired: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        }
    }

}
