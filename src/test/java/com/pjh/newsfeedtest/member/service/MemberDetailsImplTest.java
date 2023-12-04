package com.pjh.newsfeedtest.member.service;

import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberDetailsImplTest {
    private MemberDetailsImpl memberDetails;

    @BeforeEach
    @DisplayName("[MemberDetails] setup")
    void setUp() {
        // 테스트를 위한 Member 객체 생성
        Member member = Member.builder()
                .username("testUser")
                .password("encdoedPassword")
                .build();

        // MemberDetailsImpl 객체 생성
        memberDetails = new MemberDetailsImpl(member);
    }

    @Test
    @DisplayName("[MemberDetails] getUsername()")
    void getUsernameTest() {
        // 테스트에서 설정한 값과 일치하는지 확인
        assertEquals("testUser", memberDetails.getUsername());
    }

    @Test
    @DisplayName("[MemberDetails] getPassword()")
    void getPassword() {
        // 테스트에서 설정한 값과 일치하는지 확인
        assertEquals("encdoedPassword", memberDetails.getPassword());
    }

    @Test
    @DisplayName("[MemberDetails] getAuthoritites()")
    void getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = memberDetails.getAuthorities();
        assertEquals(0, authorities.size());
    }

    @Test
    @DisplayName("[MemberDetails] isAccountNonExpired()")
    void isAccountNonExpired() {
        assertTrue(memberDetails.isAccountNonExpired());
    }

    @Test
    @DisplayName("[MemberDetails] isAccountNonLocked()")
    void isAccountNonLocked() {
        assertTrue(memberDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("[MemberDetails] isCredentialsNonExpired()")
    void isCredentialsNonExpired() {
        assertTrue(memberDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("[MemberDetails] isEnabled()")
    void isEnabled() {
        assertTrue(memberDetails.isEnabled());
    }
}
