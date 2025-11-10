package com.otakumap.global.security.service;

import com.otakumap.global.security.util.PrincipalDetailsService;
import com.otakumap.global.security.jwt.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextService {

    private final PrincipalDetailsService principalDetailsService;
    private final JwtProvider jwtProvider;

    /**
     * Access Token에서 userId를 추출하고,
     * user로 사용자 인증 객체를 생성하여 SecurityContextHolder에 추가합니다.
     * @param accessToken 전달된 Access Token
     */
    public void createAuthContext(String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken);
        UserDetails userDetails = principalDetailsService.loadUserByUsername(String.valueOf(userId));

        System.out.println(userDetails);
        // Authentication 객체를 SecurityContext에 저장합니다.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        System.out.println(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}