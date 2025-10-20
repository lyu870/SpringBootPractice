package com.hello.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
        // 유저가 보낸 아이디비번이름 DB에 저장하기
        memberService.saveMember(username, password, displayName);
        return "register.html";
    }

    @GetMapping("/login")
    public String login() {
        var result = memberRepository.findByUsername("asdf");
        System.out.println(result.get().getDisplayName());
        return "login.html";
    }
}
