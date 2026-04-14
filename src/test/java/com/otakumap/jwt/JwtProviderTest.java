//package com.otakumap.jwt;
//
//import com.otakumap.global.security.jwt.util.JwtProvider;
//import io.jsonwebtoken.Claims;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//class JwtProviderTest {
//
//    private JwtProvider jwtProvider;
//
//    @Test
//    @DisplayName("사용자 정보로 JWT 토큰을 만들고 다시 정보를 해석한다.")
//    void testCreateJwtAndParseClaims() {
//        //given
//        Long userId = 1L;
//
//        //when
//        String token = jwtProvider.createAccessToken(userId);
//
//        //then
//        assertThat(token).isNotNull();
//    }
//
//    @Test
//    @DisplayName("유효한 토큰에서 클레임을 추출한다.")
//    void parseClaims() {
//        // given
//        Long userId = 1L;
//        String token = jwtProvider.createAccessToken(userId);
//
//        // when
//        Claims result = jwtProvider.getClaims(token);
//
//        // then
//        assertThat(result.getSubject()).isEqualTo(userId);
//    }
//
//    @Test
//    @DisplayName("잘못된 내용의 토큰이 들어왔을 때 예외를 발생시킨다.")
//    void testInvalidToken() {
//        // given
//        String invalidToken = "invalid.token.here";
//
//        // when & then
//        assertThrows(RuntimeException.class, () -> jwtProvider.getClaims(invalidToken));
//        assertThrows(RuntimeException.class, () -> jwtUtil.getOauthId(invalidToken));
//        assertThrows(RuntimeException.class, () -> jwtUtil.getRole(invalidToken));
//        assertThrows(RuntimeException.class, () -> jwtUtil.isExpired(invalidToken));
//    }
//
//    @Test
//    @DisplayName("유효한 토큰은 검증을 통과한다.")
//    void validateValidToken() {
//        //given
//        Long userId = 1L;
//        String token = jwtProvider.createAccessToken(userId);
//
//        //when
//        verify(jwtProvider.validateAccessToken(token), times(1));
//
//        // hen
//        assertThat(result).isTrue();
//    }
//
//}
