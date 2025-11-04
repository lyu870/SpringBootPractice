package com.hello.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

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
}
