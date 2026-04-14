package com.otakumap.auth;

import com.otakumap.domain.auth.dto.SignUpRequestDTO;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.security.jwt.dto.LoginRequestDTO;
import org.springframework.http.ResponseCookie;

public class UserFixture {

    public static final SignUpRequestDTO MOCK_SIGN_UP_REQUEST_DTO = new SignUpRequestDTO("김이름", "otakumap1234", "otakumap1234@naver.com", "encodedPass", "encodedPass");

    public static final User mockUser = UserConverter.toUser(MOCK_SIGN_UP_REQUEST_DTO);

    public static final User loginUser = UserConverter.toUser(1L, "otakumap1234");

    public static final LoginRequestDTO MOCK_LOGIN_REQUEST_DTO = new LoginRequestDTO("otakumap1234","otakumap1234!");

    public static ResponseCookie createRefreshTokenCookie(String cookieName, String refreshToken) {
        return ResponseCookie.from(cookieName, refreshToken)
                .path("/")
                .httpOnly(true)
                .build();
    }

}
