package com.example.security1.controller;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Indexcontroller {

    private final UserRepository userRepository;

    public Indexcontroller(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    //@AuthenticationPrincipal PrincipalDetails principalDetails // DI(의존성 주입)
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성 주입)
        System.out.println("/test/login==================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetails.getUser());

        System.out.println("userDetails : " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    /**
     * // 구글 로그인 테스트
     * @param authentication : Authentication 객체
     * @author Dong-Joon Oh
     */
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauth(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth) { // DI(의존성 주입)
        System.out.println("/test/oauth/login==================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + oAuth2User.getAuthorities());
        System.out.println("oAuth2User : " + oAuth.getAttributes());

        return "Oauth 세션 정보 확인하기";
    }


    @GetMapping({"","/"})
    public String index() {
        //머스터치 기본폴더 src/main/resources/
        //뷰리졸버 설정 : templates (prefix), .mustache (suffix) 생략가능
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public  String user(@AuthenticationPrincipal PrincipalDetails principalDetails ) {
        System.out.println("principalDetails : " + principalDetails.getUser());
        return "유저 페이지입니다.";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "어드민 페이지입니다.";
    }

    //@PostAuthorize("hasRole('ROLE_MANAGER')")
    //@PreAuthorize("hasRole('ROLE_MANAGER')")
    @Secured("ROLE_MANAGER")
    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "매니저 페이지입니다.";
    }

    // 스프링시큐리티 해당주소를 낚아채버림 - > SecurityConfig 파일 생성 후 작동안함
    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String  join() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String joinProc(User user) {
        System.out.println("회원가입 진행 : " + user);

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        user.setPassword(encPassword);
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 메소드 실행 전에 권한을 체크 여러개 가능
    @GetMapping("/data")
    @ResponseBody
    public String data() {
        return "데이터정보";
    }
}
