package com.daengdaeng_eodiga.project.global.Security.config;

import com.daengdaeng_eodiga.project.global.Security.dto.GoogleResponse;
import com.daengdaeng_eodiga.project.global.Security.dto.KakaoResponse;
import com.daengdaeng_eodiga.project.global.Security.dto.OAuth2Response;
import com.daengdaeng_eodiga.project.oauth.OauthProvider;
import com.daengdaeng_eodiga.project.oauth.dto.UserOauthDto;
import com.daengdaeng_eodiga.project.user.repository.UserRepository;
import com.daengdaeng_eodiga.project.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * OAuth2 로그인 후 사용자 정보를 로드하는 메소드.
     * @author 김승환
     *
     * - 클라이언트 등록 ID를 확인하여 사용자의 프로필 정보를 가져옵니다.
     * - 사용자가 이미 존재하는지 확인하고, 존재하지 않으면 회원가입 페이지로 리디렉션을 유도합니다.
     * - 사용자 정보가 유효하다면, CustomOAuth2User 객체를 반환하여 인증 처리를 합니다.
     *
     * @param userRequest OAuth2 사용자 요청 객체
     * @return 인증된 사용자 정보를 담은 {@link OAuth2User}
     * @throws OAuth2AuthenticationException 인증 중 오류가 발생한 경우 예외를 던집니다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        OauthProvider provider=null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
             provider= OauthProvider.kakao;

        } else if (registrationId.equals("google")) {
             oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
             provider= OauthProvider.google;
        }
        String email = oAuth2Response.getEmail();


        Optional<User> existData = userRepository.findByEmailAndOauthProvider(email,provider);
        if (existData.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    "REDIRECT_TO_SIGNUP",
                    "REDIRECT_TO_SIGNUP: email=" + email + ", provider=" + provider,
                    null
            ));
        } else {
            if(existData.get().getDeletedAt()!=null){

                throw new OAuth2AuthenticationException(new OAuth2Error(
                    "DELETED_USER",
                    "DELETED_USER: email=" + email + ", provider=" + provider,
                    null
                ));
            }
            User user = existData.get();
            UserOauthDto userDTO = new UserOauthDto();
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getEmail());
            userDTO.setProvider(user.getOauthProvider());
            return new CustomOAuth2User(userDTO);
        }
    }
}
