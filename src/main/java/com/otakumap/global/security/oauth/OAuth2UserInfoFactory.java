package com.otakumap.global.security.oauth;

import com.otakumap.domain.user.entity.enums.SocialType;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.security.oauth.userInfo.GoogleOAuth2UserInfo;
import com.otakumap.global.security.oauth.userInfo.KakaoOAuth2UserInfo;
import com.otakumap.global.security.oauth.userInfo.NaverOAuth2UserInfo;
import com.otakumap.global.security.oauth.userInfo.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    /**
     * 소셜 로그인별로 사용자 정보를 매핑하는 팩토리입니다.
     *
     * @param socialType 소셜 로그인 별 Enum Type
     * @param attributes 사용자 정보 JSON
     * @return 소셜 로그인 제공자에 맞게 파싱
     */
    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        return switch (socialType) {
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new AuthHandler(ErrorStatus.UNSUPPORTED_PROVIDER);
        };
    }
}