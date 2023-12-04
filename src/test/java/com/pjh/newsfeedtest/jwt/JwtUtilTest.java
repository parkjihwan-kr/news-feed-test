package com.pjh.newsfeedtest.jwt;

import com.pjh.newsfeedtest.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest{

    @Autowired
    JwtUtil jwtUtil;
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil.init();
    }

    @Test
    @DisplayName("[Security] [jwt] createToken() test")
    public void createTokenTest(){
        String token = jwtUtil.createToken("TEST_USER_NAME");
        assertNotNull(token);
    }

    @Test
    @DisplayName("[Security] [jwt] addJwtToCookie() test")
    public void addJwtToCookieTest() {
        String token = jwtUtil.createToken("TEST_USER_NAME");
        jwtUtil.addJwtToCookie(token, response);

        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("[Security] [jwt] substringToken() test")
    public void substringTokenTest() {
        String token = jwtUtil.createToken("TEST_USER_NAME");
        String fullToken = JwtUtil.BEARER_PREFIX + token;

        String result = jwtUtil.substringToken(fullToken);

        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    @DisplayName("[Security] [jwt] validateToken() test Success")
    void validateTokenSuccessTest() {
        // given
        String token = jwtUtil.createToken("TEST_USER_NAME").substring(7);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("[Security] [jwt] validateToken() Expired Token test")
    void validateTokenExpiredTokenTest() {
        // given
        // token 직접 만들기
        Date expirationTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

        log.info("expirationTime : "+expirationTime);

        String token = "BEARER " + Jwts.builder()
                .setSubject("TEST_USER_NAME")
                .setExpiration(expirationTime)
                .compact();

        System.out.println("Generated Token: " + token);

        // when
        boolean isValid = jwtUtil.validateToken(token.substring(7));

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("[Security] [jwt] validateToken() unsupportedToken test")
    public void validateTokenUnsupportedTokenTest() {
        // given
        Date expirationTime = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        log.info("expirationTime : "+expirationTime);

        String unsupportedToken = "BEARER " + Jwts.builder()
                .setSubject("TEST_USER_NAME")
                .setExpiration(expirationTime)
                .compact();
        // signature가 없으니까 invalidToken

        // when
        boolean isValid = jwtUtil.validateToken(unsupportedToken.substring(7));

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("[Security] [jwt] getUserInfoFromToken() test")
    public void getUserInfoFromTokenTest() {
        String token = jwtUtil.createToken("TEST_USER_NAME");
        Claims claims = jwtUtil.getUserInfoFromToken(token.substring(7));

        assertNotNull(claims);
        assertEquals("TEST_USER_NAME", claims.getSubject());
    }
}
