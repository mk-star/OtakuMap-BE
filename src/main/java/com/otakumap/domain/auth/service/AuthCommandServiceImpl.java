package com.otakumap.domain.auth.service;

import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.AuthResponseDTO;
import com.otakumap.global.security.jwt.dto.JwtDTO;
import com.otakumap.global.security.PrincipalDetails;
import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.domain.image.entity.Image;
import com.otakumap.domain.image.repository.ImageRepository;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final MailService mailService;
    private final JwtProvider jwtProvider;

    @Override
    public User signup(AuthRequestDTO.SignupDTO request) {
        if(!request.getPassword().equals(request.getPasswordCheck())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }
        User newUser = UserConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }

    @Override
    public AuthResponseDTO.LoginResultDTO login(AuthRequestDTO.LoginDTO request) {
        User user = userRepository.findByUserId(request.getUserId()).orElseThrow(() -> new AuthHandler(ErrorStatus.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_EQUAL);
        }

        Long userId = user.getId();

        // 로그인 성공 시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        return UserConverter.toLoginResultDTO(userId, accessToken, refreshToken);
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
        String authCode = (String) redisUtil.get("auth:" + request.getEmail() + ":" + "signup");
        if (authCode == null) {
            throw new AuthHandler(ErrorStatus.EMAIL_CODE_EXPIRED);
        }
        if (!authCode.equals(request.getCode())) {
            throw new AuthHandler(ErrorStatus.CODE_NOT_EQUAL);
        }
        return true;
    }

    @Override
    public JwtDTO reissueToken(JwtDTO request) {
        try {
            jwtProvider.validateRefreshToken(request);
            return jwtProvider.reissueToken(request);
        } catch (ExpiredJwtException eje) {
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (IllegalArgumentException iae) {
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    @Override
    public void logout(JwtDTO request) {
        try {
            // 블랙리스트에 저장
            String accessToken = request.getAccessToken();
            String key = "AT::" + accessToken;
            redisUtil.set(key, "logout");
            redisUtil.expire(key, jwtProvider.getExpTime(accessToken), TimeUnit.MILLISECONDS);

            // RefreshToken 삭제
            redisUtil.delete("RT::" + request.getRefreshToken());

        } catch (ExpiredJwtException e) {
            throw new AuthHandler(ErrorStatus.TOKEN_EXPIRED);
        }
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
        String authCode = (String) redisUtil.get("auth:" + user.getEmail() + ":" + "findPassword");
        if (authCode == null) {
            throw new AuthHandler(ErrorStatus.EMAIL_CODE_EXPIRED);
        }
        if (!authCode.equals(request.getCode())) {
            throw new AuthHandler(ErrorStatus.CODE_NOT_EQUAL);
        }
        return true;
    }
}
