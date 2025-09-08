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
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(SocialType.KAKAO.toString())) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialType.NAVER.toString())) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialType.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new AuthHandler(ErrorStatus.UNSUPPORTED_PROVIDER);
    }
}