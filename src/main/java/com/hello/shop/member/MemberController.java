package com.hello.shop.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    @PostMapping("/member")
    public String addMember(String username, String password, String displayName) throws Exception {
        // 유저가 입력한 아이디비번이름 DB에 저장하기
        memberService.saveMember(username, password, displayName);
        return "register.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    // 
    @GetMapping("/my-page")
    public String myPage(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        System.out.println(user.getDisplayName());
        return "mypage.html";
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public MemberDto user(@PathVariable Long id) {
        return memberService.getMemberDtoById(id);
    }

    @PostMapping("/login/jwt")
    @ResponseBody
    public String loginJWT(@RequestBody Map<String, String> data,
                           HttpServletResponse response){

        var authToken = new UsernamePasswordAuthenticationToken(
                data.get("username"), data.get("password")
        );
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth); // 수동로그인 방식이기때문에 유저정보를 따로 받아옴.

        var jwt = JwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());
        System.out.println(jwt);

        var cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(10);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return jwt;
        //        SecurityContextHolder.getContext().getAuthentication(); // → Auth변수와 동일한 역할.
    }

    @GetMapping("/my-page/jwt")
    @ResponseBody
    public String myPageJWT(Authentication auth) {
//        JWT 까보고 로그인잘되어있으면 마이페이지 보내주기~
        var user = (CustomUser) auth.getPrincipal();
        System.out.println(user);
        System.out.println(user.getDisplayName());
        System.out.println(user.getAuthorities());

        return "login.html";
    }

}
