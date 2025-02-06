package com.backstage.curtaincall.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (필요에 따라 설정)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()  // 모든 요청 인증 없이 허용
                )
                .formLogin(form -> form.disable())  // 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()); // 기본 HTTP 인증 비활성화

        return http.build();
    }

}
