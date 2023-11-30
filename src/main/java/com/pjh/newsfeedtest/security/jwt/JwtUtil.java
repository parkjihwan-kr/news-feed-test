package com.pjh.newsfeedtest.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
@Component
@Slf4j
public class JwtUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    // 인가 헤더
    public static final String AUTHORIZATION_KEY = "auth";
    // 인가 키
    public static final String BEARER_PREFIX = "Bearer ";
    // token 식별자
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    // 토큰 만료 시간

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init(){
        log.info("init start!");
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
        // key값에 Base64 Encode 한 SecretKey를 디코드하여 바이트 배열에 넣음
    }

    public String createToken(String username){
        Date date = new Date();
        log.info("createToken method start");
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)               // 사용자 식별자값(ID), 나중에 pK로 식별자값 넣기, 필수
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간, 필수
                        .setIssuedAt(date)                  // 발급일(선택사항)
                        .signWith(key, signatureAlgorithm)  // 암호화 알고리즘, 필수
                        .compact();
    }
    // JWT 생성 메서드(cookie 객체 생성 또는 reponseHeader에서 내보는 방법)

    public void addJwtToCookie(String token, HttpServletResponse res){
        // 쿠키는 공백 불가
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            log.info("인증 헤더 : "+AUTHORIZATION_HEADER+"| Token : "+token);
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");
            log.info("cookie 추가");
            // Response 객체에 Cookie 추가
            res.addCookie(cookie);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    public String substringToken(String tokenValue){
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            log.info("substringToken");
            // 공백과 null이 되면 안되고 Bearer로 시작하는지
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    public boolean validateToken(String token) {
        try {
            log.info("validateToken");
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
    // JWT 검증

    public Claims getUserInfoFromToken(String token) {
        log.info("getUserInfoFromToken");
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    // JWT 사용자 정보를 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        log.info("getTokenFromRequest");
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
