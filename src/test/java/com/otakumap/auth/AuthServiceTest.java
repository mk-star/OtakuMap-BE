package com.otakumap.auth;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.otakumap.domain.auth.service.AuthCommandServiceImpl;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.security.jwt.dto.TokenResponseDTO;
import com.otakumap.global.security.jwt.util.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.otakumap.auth.UserFixture.*;
import static com.otakumap.global.apiPayload.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("이미 존재하는 아이디로 가입 시 AuthHandler 예외를 발생시킨다.")
    void signUp_duplicatedUserId_throwsException() {
        // given
        given(userRepository.existsByUserId(anyString())).willReturn(true); // 중복이다

        // when & then
        // 중복이면 AuthHandler 예외가 발생
        assertThatThrownBy(() -> authCommandService.signup(MOCK_SIGN_UP_REQUEST_DTO))
                .isInstanceOf(AuthHandler.class)
                .hasMessage(USERID_ALREADY_EXISTS.getMessage());

        // save가 호출되지 않는지 검증 가능
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호가 맞지 않으면 예외 발생")
    void signUp_notEqualPassword_throwsException() {
        //given
        given(userRepository.existsByUserId(anyString())).willReturn(false); // 중복 아님

        // when & then
        // 중복이면 AuthHandler 예외가 발생
        assertThatThrownBy(() -> authCommandService.signup(MOCK_SIGN_UP_REQUEST_DTO))
                .isInstanceOf(AuthHandler.class)
                .hasMessage(PASSWORD_NOT_EQUAL.getMessage());

        // save가 호출되지 않는지 검증 가능
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입에 성공한다.")
    void signUp_success() {
        // given
        given(userRepository.existsByUserId(anyString())).willReturn(false); // 중복 아님
        given(passwordEncoder.encode(anyString())).willReturn("encodedPass"); // 암호 수동 설정
        given(userRepository.save(any(User.class))).willReturn(mockUser);

        // when
        User savedUser = authCommandService.signup(MOCK_SIGN_UP_REQUEST_DTO);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(MOCK_SIGN_UP_REQUEST_DTO.name());
        assertThat(savedUser.getEmail()).isEqualTo(MOCK_SIGN_UP_REQUEST_DTO.email());
        assertThat(savedUser.getPassword()).isEqualTo("encodedPass"); // 인코딩된 비밀번호 검증

        then(userRepository).should(times(1)).existsByUserId(anyString());
        then(userRepository).should(times(1)).save(any(User.class));
    }


    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 AuthHandler 예외를 발생시킨다.")
    void login_notExistUserId_throwsException() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());
  
        // when & then
        assertThatThrownBy(() -> authCommandService.login(MOCK_LOGIN_REQUEST_DTO))
                .isInstanceOf(AuthHandler.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 틀린 경우 AuthHandler 예외를 발생시킨다.")
    void login_notEqualPassword_throwsException() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authCommandService.login(MOCK_LOGIN_REQUEST_DTO))
                .isInstanceOf(AuthHandler.class)
                .hasMessage(PASSWORD_NOT_EQUAL.getMessage());
    }

    // https://github.com/dnd-side-project/dnd-10th-7-backend/blob/develop/src/test/java/com/sendback/domain/user/service/UserServiceTest.java
    @Test
    @DisplayName("회원이 로그인에 성공한다.")
    void login_success() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(loginUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtProvider.createAccessToken(anyLong())).willReturn("valid accessToken");
        given(jwtProvider.createRefreshToken()).willReturn("valid refreshToken");
        given(jwtProvider.generateRefreshTokenCookie(anyLong(), anyString())).willReturn(createRefreshTokenCookie("refreshToken", "validRefreshToken"));

        //when
        TokenResponseDTO response = authCommandService.login(MOCK_LOGIN_REQUEST_DTO);

        //then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(loginUser.getId());
        assertThat(response.accessToken()).isEqualTo("valid accessToken");

//        then(userRepository).should().findByUserId(request.getUserId());
//        then(passwordEncoder).should().matches(request.getPassword(), mockUser.getPassword());
//        then(jwtProvider).should().createAccessToken(mockUser.getId());
//        then(jwtProvider).should().createRefreshToken();
//        then(jwtProvider).should().generateRefreshTokenCookie(mockUser.getId(), "refreshToken123");
    }

}
