package com.hello.shop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/**")
                .permitAll().anyRequest().permitAll()
        );

        http.formLogin((formLogin) -> formLogin.loginPage("/login")
                .loginProcessingUrl("/login") // POST /login 인증 처리
                .defaultSuccessUrl("/", true) // 성공 시 이동
                .failureUrl("/login?error") // 실패 시 이동 (기본값)
                .permitAll()
        );

        http.logout( logout -> logout.logoutUrl("/logout") );

        return http.build();
    }

    // csrf기능 활성화시 해당 주석제거
//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setHeaderName("X-XSRF-TOKEN");
//        return repository;
//    }
}