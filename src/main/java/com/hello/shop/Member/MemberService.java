package com.hello.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveMember(String username, String password, String displayName) throws Exception {
        // 빠른 실패로 불필요한 DB 호출/예외 줄이기
        if (username == null || password == null || displayName == null) {
            throw new Exception("입력값이 비어있습니다.");
        }
        if (username.length() < 8 || password.length() < 8) {
            throw new Exception("ID/Password가 너무 짧음(8자이상).");
        }

        var result = memberRepository.findByUsername(username);
        if (result.isPresent()) {
            throw new Exception("존재하는아이디");
        }

        Member member = new Member();
        member.setUsername(username);
        var hash = passwordEncoder.encode(password); // 패스워드 암호화.
        member.setPassword(hash);
        member.setDisplayName(displayName);
        memberRepository.save(member);
    }
}
