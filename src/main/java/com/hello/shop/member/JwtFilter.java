package com.hello.shop.member;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null){ // 쿠키에 값이 없다면 다음필터실행.
            filterChain.doFilter(request, response);
            return;
        }

        var jwtCookie = "";
        for (int i = 0; i < cookies.length; i++){
            if (cookies[i].getName().equals("jwt")){
                jwtCookie = cookies[i].getValue();
            }
        }

        // JWT가 유효한지 확인 try, catch
        Claims claim;
        try {
            claim = JwtUtil.extractToken(jwtCookie);
        } catch (Exception e) {
            System.out.println("유효하지 않은 값입니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // JWT이상없으면 auth변수에 유저정보 추가하고 권한넣기
        var arr = claim.get("authorities").toString().split(",");
        var authorities = Arrays.stream(arr)
                .map(a -> new SimpleGrantedAuthority(a)).toList();

        String username = String.valueOf(claim.get("username")); // 사용자정보 JWT에서 읽기
        String displayName = String.valueOf(claim.get("displayName")); // 사용자정보 JWT에서 읽기

        Number idNum = (Number) claim.get("id"); // Integer/Long 모두 대비
        Long id = (idNum != null) ? idNum.longValue() : null;


        CustomUser customUser = new CustomUser( // CustomUser 생성자 맞게 생성 (password는 placeholder)
                username,
                "N/A",
                authorities,
                displayName,
                id
        );


        // 인증토큰 구성 및 SecurityContext에 데이터 넣기
        var authToken = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                authorities
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
