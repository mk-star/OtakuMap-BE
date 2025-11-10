//package com.otakumap.global.security.jwt.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.otakumap.domain.auth.dto.AuthRequestDTO;
//import com.otakumap.global.apiPayload.code.status.ErrorStatus;
//import com.otakumap.global.security.util.PrincipalDetails;
//import com.otakumap.global.security.jwt.dto.LoginRequestDTO;
//import com.otakumap.global.security.jwt.util.JwtProvider;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//import java.util.Map;
//
//public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtProvider jwtProvider;
//
//    public LoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
//        this.authenticationManager = authenticationManager;
//        this.jwtProvider = jwtProvider;
//    }
//
//    /**
//     * 로그인 요청 시 사용자 인증 처리
//     */
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        // 클라이언트 요청에서 username, password 추출
//        try {
//            LoginRequestDTO loginRequest = new ObjectMapper().readValue(request.getInputStream(),
//                        LoginRequestDTO.class);
//
//            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
//            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.userId(), loginRequest.password());
//
//            // AuthenticationManager를 통해 인증 수행
//            return authenticationManager.authenticate(authRequest);
//        } catch (IOException e) {
//            throw new BadCredentialsException(ErrorStatus.INVALID_INPUT_VALUE.getMessage());
//        }
//    }
//
//    /**
//     * 로그인 성공 시 JWT 토큰 발급
//     */
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
//        // 인증된 사용자 정보 가져오기
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        Long userId = principalDetails.getUser().getId();
//
//        // Access Token, Refresh Token 생성
//        String accessToken = jwtProvider.createAccessToken(userId);
//        ResponseCookie cookie = jwtProvider.generateRefreshTokenCookie(userId);
//
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//
//        Map<String, String> tokenMap = Map.of("accessToken", accessToken);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(), tokenMap);
//    }
//
//    /**
//     * 로그인 실패 시 401 응답 반환
//     */
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) {
//        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 응답
//    }
//
//}