package com.pjh.newsfeedtest.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.newsfeedtest.member.dto.LoginDto;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login");
        // login process -> /api/user/login ... controller를 접근을 하지 않고(filter역할) jwtAuthentioncationFilter로 사용해서 만듦.
        // 그럼으로 js또는 html에서 사용하는 login.html에서 ajax 비동기 통신에서 method : "post", url : `/api/user/login`
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // HttpServletRequest -> 클라이언트에서 서버로 http요청을 캡슐화 URL, HTTP메서드, 요청 헤더, 쿼리 문자열, 세션정보, 요청 파라미터, 바디 등등
        // HttpServletResponse -> 서버에서 클라이언트로 http응답을 보냄, 필요한 메서드 및 속성을 보내고 응답 상태코드 200, 404등등을 보냄
        log.info("로그인 시도");
        try {

            LoginDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);
            // 해당 사항 LoginDto를 만들때 활성화

            log.info("loginDto.getUsername() : "+loginDto.getUsername()+" || loginDto.getPassword() : "+loginDto.getPassword());
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        // loginDto 활성화
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((MemberDetailsImpl) authResult.getPrincipal()).getUsername();
        String token = jwtUtil.createToken(username);
        // 토큰의 파라미터로 username(principal().getUser() -> 인증된 유저), role -> (authResult.getPrincipal().getUser().getRole() 인증된 유저의 RoleType : ADMIN || USER)
        jwtUtil.addJwtToCookie(token, response);
        // TOKEN발급, jwt를 cookie속에 넣어줌
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
