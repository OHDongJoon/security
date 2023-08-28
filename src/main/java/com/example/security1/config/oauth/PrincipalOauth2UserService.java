package com.example.security1.config.oauth;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.config.oauth.provider.FaceBookUserInfo;
import com.example.security1.config.oauth.provider.GoogleUserInfo;
import com.example.security1.config.oauth.provider.NaverUserInfo;
import com.example.security1.config.oauth.provider.OAuth2UserInfo;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    @Autowired
    private final UserRepository userRepository;




    public PrincipalOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest 1: " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 확인 가능
        System.out.println("userRequest 2 : " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
        // userRequest 정보  loadUser함수 호출 -> 구글로부터 회원프로필 받아준다.
        System.out.println("userRequest 3: " + super.loadUser(userRequest).getAuthorities());

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());

        } else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
             oAuth2UserInfo = new FaceBookUserInfo(oauth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oauth2User.getAttributes().get("response"));
            System.out.println("oAuth2UserInfo : " + oauth2User.getAttributes());
        } else {
            System.out.println("우리는 구글 페이스북 네이버만 지원해요 ㅎㅎ");
        }

        String provider = oAuth2UserInfo.getProvider(); // google or facebook or naver
        String providerId = oAuth2UserInfo.getProviderId(); // google의 primary key (sub) or facebook의 primary key (id) or naver의 primary key (id)
        String userName = providerId + "_" + provider; // google_123123123
        String password = "1234";
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(userName);
        if (userEntity == null) {
            System.out.println("로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(userName)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
            System.out.println("userEntity : " + userEntity);
        } else {
            System.out.println(" 로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
        }
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
