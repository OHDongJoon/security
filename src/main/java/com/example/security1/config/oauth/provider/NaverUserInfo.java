package com.example.security1.config.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes; // oauth2User.getAttributes()
    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id"); // google의 primary key
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email"); // google의 primary key
    }

    @Override
    public String getName() {
        return (String) attributes.get("name"); // google의 primary key

    }
}
