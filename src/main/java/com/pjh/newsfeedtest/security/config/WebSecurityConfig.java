package com.pjh.newsfeedtest.security.config;

import com.pjh.newsfeedtest.security.jwt.JwtAuthenticationFilter;
import com.pjh.newsfeedtest.security.jwt.JwtAuthorizationFilter;
import com.pjh.newsfeedtest.security.jwt.JwtUtil;
import com.pjh.newsfeedtest.security.service.MemberDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final MemberDetailsServiceImpl memberDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    //AuthenticationManager 만들기

    public WebSecurityConfig(JwtUtil jwtUtil, MemberDetailsServiceImpl memberDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.memberDetailsService = memberDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
        // AuthenticationManager를 빈 수동 등록(인증을 위해)
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
        // Jwt를 사용한 인증을 처리하는 필터를 빈으로 등록
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, memberDetailsService);
        // Jwt를 사용한 인가를 처리하는 필터를 빈으로 등록
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("api/user/**","user/**").permitAll()
                        // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/user/login-page").permitAll()
        ).logout(logout ->
                logout
                        .logoutUrl("/logout")                   // 로그아웃 URL 설정
                        .logoutSuccessUrl("/user/login-page")   // 로그아웃 성공 시 이동할 페이지
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        // JwtAuthenticationFilter.class전에 jwtAuthorizationFilter() 수행
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // UsernamePasswordAuthenticationFilter.class전에 jwtAuthenticationFilter() 수행
        http.exceptionHandling((exceptionHandling)->
                exceptionHandling
                        .accessDeniedPage("/forbidden.html")
        );
        // 접근 불가페이지
        return http.build();
    }
}