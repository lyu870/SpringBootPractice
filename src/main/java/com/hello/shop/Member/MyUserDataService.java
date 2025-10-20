package com.hello.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Spring Security는 DB의 어느데이터가 passowrd인지 인식불가.
// DB에 있던 유저의 password찾아와주고 라이브러리에 넘겨주기.

@Service
@RequiredArgsConstructor
public class MyUserDataService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var result = memberRepository.findByUsername(username);
        if (result.isEmpty()){
            throw new UsernameNotFoundException("그런 아이디 없음");
        }
        var user = result.get();
        List<GrantedAuthority> 권한목록 = new ArrayList<>();
        권한목록.add(new SimpleGrantedAuthority("일반유저"));
        return new User(user.getUsername(), user.getPassword(), 권한목록);
    }
}
