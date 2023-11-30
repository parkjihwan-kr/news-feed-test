package com.pjh.newsfeedtest.security.jwt;

import com.pjh.newsfeedtest.security.service.MemberDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // 들어온 jwt response
    private final JwtUtil jwtUtil;
    private final MemberDetailsServiceImpl memberDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, MemberDetailsServiceImpl memberDetailsService) {
        this.jwtUtil = jwtUtil;
        this.memberDetailsService = memberDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getTokenFromRequest(req);

        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info(tokenValue);

            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        log.info("setAuthentication : "+username);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 왜 빈공간을 만들지?
        Authentication authentication = createAuthentication(username);
        // Authentication 객체에 username은 인증되어진 사용자다, 라는걸 넣어줌
        // UserDetails userDetails = userDetailsService.loadByUsername(username);을 통해 인증된 사용자임을 증명
        context.setAuthentication(authentication);
        // securityContext에 authentication(인증된 사용자이라는 객체)을 넣어줌
        SecurityContextHolder.setContext(context);
        //
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}