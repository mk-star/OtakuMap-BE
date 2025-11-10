package com.otakumap.global.security.oauth;

import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.entity.enums.Role;
import com.otakumap.domain.user.entity.enums.SocialType;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.security.util.PrincipalDetails;
import com.otakumap.global.security.oauth.userInfo.OAuth2UserInfo;
import com.otakumap.global.util.UuidGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2User oAuth2User = super.loadUser(userRequest); // 인증된 사용자의 정보를 담고 있는 객체

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 어떤 OAuth2(카카오, 네이버, 구글)가 사용되었는지 식별
        SocialType socialType = SocialType.valueOf(registrationId.toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User.getAttributes());

        User user = getUser(oAuth2UserInfo, socialType);

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private User getUser(OAuth2UserInfo oAuth2UserInfo, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, oAuth2UserInfo.getId()).orElse(null);

        if(findUser == null) {
            return toEntity(oAuth2UserInfo, socialType);
        }
        return findUser;
    }

    private User toEntity(OAuth2UserInfo oAuth2UserInfo, SocialType socialType) {
        User user = User.builder()
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId()) // 소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .nickname(UuidGenerator.generateUuid())
                .profileImage(oAuth2UserInfo.getImageUrl())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }
}
