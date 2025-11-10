package com.otakumap.domain.auth.controller;

import com.otakumap.domain.auth.dto.AuthRequestDTO;
import com.otakumap.domain.auth.dto.AuthResponseDTO;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.global.security.jwt.dto.TokenPair;
import com.otakumap.domain.auth.service.AuthCommandService;
import com.otakumap.domain.auth.service.AuthQueryService;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.global.apiPayload.ApiResponse;
import com.otakumap.global.security.jwt.dto.RefreshTokenRequestDTO;
import com.otakumap.global.security.jwt.util.JwtProvider;
import com.otakumap.global.security.service.TokenService;
import com.otakumap.global.security.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import static com.otakumap.global.security.util.CookieUtil.REFRESH_TOKEN_COOKIE;
import static com.otakumap.global.security.util.TokenUtil.extractTokenFromHeader;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입 API
     * 사용자 정보를 받아서 DB에 저장합니다.
     *
     * @param request 회원가입 시 입력한 사용자 정보
     * @return 사용자 정보
     */
    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @PostMapping("/signup")
    public ApiResponse<AuthResponseDTO.SignupResultDTO> signup(@RequestBody @Valid AuthRequestDTO.SignupDTO request) {
        return ApiResponse.onSuccess(UserConverter.toSignupResultDTO(authCommandService.signup(request)));
    }

    /**
     * 로그인 API
     * 아이디와 비밀번호를 받아서 로그인합니다.
     *
     * @param request 회원가입 시 입력한 사용자 정보
     * @return 사용자 정보
     */
    @Operation(summary = "일반 로그인", description = "일반 로그인 기능입니다.")
    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> login(
            @RequestBody @Valid AuthRequestDTO.LoginDTO request,
            HttpServletResponse response
    ) {

        // 아이디, 비밀번호 비교 후 token 반환
        TokenPair tokenPair = authCommandService.login(request);

        // 새로운 쿠키 설정
        response.addHeader(HttpHeaders.SET_COOKIE, tokenPair.cookie().toString());

        return ApiResponse.onSuccess(UserConverter.toLoginResultDTO(tokenPair.id(), tokenPair.accessToken()));
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인 기능입니다.")
    @GetMapping("/check-id")
    @Parameter(name = "userId", description = "아이디")
    public ApiResponse<AuthResponseDTO.CheckIdResultDTO> checkId(@RequestParam String userId) {
        return ApiResponse.onSuccess(UserConverter.toCheckIdResultDTO(authQueryService.checkId(userId)));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인 기능입니다.")
    @GetMapping("/check-email")
    @Parameter(name = "email", description = "이메일")
    public ApiResponse<AuthResponseDTO.CheckEmailResultDTO> checkEmail(@RequestParam String email) {
        return ApiResponse.onSuccess(UserConverter.toCheckEmailResultDTO(authQueryService.checkEmail(email)));
    }

    @Operation(summary = "이메일 인증 메일 전송", description = "이메일 인증을 위한 메일 전송 기능입니다.")
    @PostMapping("/verify-email")
    public ApiResponse<String> verifyEmail(@RequestBody @Valid AuthRequestDTO.VerifyEmailDTO request) {
        authCommandService.verifyEmail(request);
        return ApiResponse.onSuccess("인증 메일이 성공적으로 전송되었습니다.");
    }

    @Operation(summary = "이메일 코드 인증", description = "회원가입 시 이메일 코드 인증 기능입니다.")
    @PostMapping("/verify-code")
    public ApiResponse<AuthResponseDTO.VerifyCodeResultDTO> verifyEmail(@RequestBody @Valid AuthRequestDTO.VerifyCodeDTO request) {
        return ApiResponse.onSuccess(UserConverter.toVerifyCodeResultDTO(authCommandService.verifyCode(request)));
    }

    /**
     * 리프레시 토큰 재발급 API
     * 유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     * 액세스 토큰이 만료된 상황에서 사용되므로 JWT 인증이 필요하지 않습니다.
     *
     * @param request 리프레시 토큰 재발급 요청
     * @return 새로 발급된 토큰 정보
     */
    @Operation(summary = "토큰 재발급", description = "accessToken이 만료 시 refreshToken을 통해 accessToken을 재발급합니다.")
    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> reissueToken(
            @RequestBody RefreshTokenRequestDTO refreshTokenRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // 재발급한 토큰
        TokenPair tokenPair = authCommandService.reissueToken(request, refreshTokenRequest);

        // 새로운 쿠키 생성
        response.addHeader(HttpHeaders.SET_COOKIE, tokenPair.cookie().toString());

        return ApiResponse.onSuccess(UserConverter.toLoginResultDTO(tokenPair.id(), tokenPair.accessToken()));
    }

    /**
     * 사용자 로그아웃 API
     * Redis에서 Refresh Token을 삭제하고 Access Token을 블랙리스트에 추가하며 쿠키를 무효화합니다.
     * @param request HTTP 요청 (Access Token 추출용)
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 로그아웃 성공 여부
     */
    @Operation(summary = "로그아웃", description = "로그아웃 기능입니다.")
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // 모든 토큰 무효화 (Refresh Token 삭제 + Access Token 블랙리스트)
        String accessToken = extractTokenFromHeader(request);
        Long userId = jwtProvider.getUserIdFromToken(accessToken);
        tokenService.logout(userId, accessToken);

        // 쿠키 무효화
        ResponseCookie invalidatedCookie = cookieUtil.createInvalidatedCookie(REFRESH_TOKEN_COOKIE);
        response.addHeader(HttpHeaders.SET_COOKIE, invalidatedCookie.toString());

        return ApiResponse.onSuccess("로그아웃 되었습니다.");
    }

//    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 입력받아 로그인을 처리합니다.")
//    @PostMapping("/social/kakao")
//    public ApiResponse<AuthResponseDTO.LoginResultDTO> kakaoLogin(@Valid @RequestBody AuthRequestDTO.SocialLoginDTO request) {
//        return ApiResponse.onSuccess(socialAuthService.login("kakao", request));
//    }
//
//    @Operation(summary = "구글 로그인", description = "구글 인가 코드를 입력받아 로그인을 처리합니다.")
//    @PostMapping("/social/google")
//    public ApiResponse<AuthResponseDTO.LoginResultDTO> googleLogin(@Valid @RequestBody AuthRequestDTO.SocialLoginDTO request) {
//        return ApiResponse.onSuccess(socialAuthService.login("google", request));
//    }
//
//    @Operation(summary = "네이버 로그인", description = "네이버 인가 코드를 입력받아 로그인을 처리합니다.")
//    @PostMapping("/social/naver")
//    public ApiResponse<AuthResponseDTO.LoginResultDTO> naverLogin(@Valid @RequestBody AuthRequestDTO.SocialLoginDTO request) {
//        return ApiResponse.onSuccess(socialAuthService.login("naver", request));
//    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기 기능입니다.")
    @GetMapping("/find-id")
    @Parameters({
            @Parameter(name = "name", description = "유저 이름"),
            @Parameter(name = "email", description = "유저 이메일")
    })
    public ApiResponse<AuthResponseDTO.FindIdResultDTO> findId(@RequestParam String name, @RequestParam String email) {
        return ApiResponse.onSuccess(UserConverter.toFindIdResultDTO(authQueryService.findId(name, email)));
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 찾기 기능입니다.")
    @PostMapping("/find-password")
    public ApiResponse<String> findPassword(@RequestBody @Valid AuthRequestDTO.FindPasswordDTO request) {
        authCommandService.findPassword(request);
        return ApiResponse.onSuccess("인증 메일이 성공적으로 전송되었습니다.");
    }

    @Operation(summary = "비밀번호 찾기 코드 인증", description = "비밀번호 찾기 시 이메일 코드 인증 기능입니다.")
    @PostMapping("/verify-password-code")
    public ApiResponse<AuthResponseDTO.VerifyCodeResultDTO> verifyPasswordCode(@RequestBody @Valid AuthRequestDTO.VerifyResetCodeDTO request) {
        return ApiResponse.onSuccess(UserConverter.toVerifyCodeResultDTO(authCommandService.verifyResetCode(request)));
    }

}
