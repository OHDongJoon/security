package com.example.security1.config.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인 진행시킴
// 로그인 진행이 완료되면 시큐리티 session을 만들어줌 (Security ContextHolder)
// 오브젝트 => Authentication 타입 객체
// Authentication 안에 User 정보가 있어야 됨
// User 오브젝트 타입 => UserDetails 타입 객체

import com.example.security1.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Security Session => Authentication => UserDetails(PrincipalDetails)
@Data
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {


    private User user; // 콤포지션

    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }




    // 해당 User의 권한을 리턴하는 곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       Collection<GrantedAuthority> collect = new ArrayList<>();
       collect.add(new GrantedAuthority() {
           @Override
           public String getAuthority() {
               return user.getRole();
           }
    });
       return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        // 우리 사이트!! 1년동안 회원이 로그인을 안하면!! 휴먼 계정으로 하기로 함

        return true;
    }


    /**
     * OAuth2User 인터페이스의 구현체
     * oAuth2User 구글 : {sub=116292517405476398901, name=오동준, given_name=동준, family_name=오, picture=https://lh3.googleusercontent.com/a/AAcHTtf60FAWwSyuTfqgeXfrdccPo6xSTHSPB7H9h-R16xZG=s96-c, email=ehdwns3536@gmail.com, email_verified=true, locale=ko}
     * @return OAuth2User 로그인을 했을 때 정보를 받아줌
     */

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * OAuth2User 인터페이스의 구현체
     * @return String
     */
    @Override
    public String getName() {
        return null;
    }
}
