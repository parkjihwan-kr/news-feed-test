package com.pjh.newsfeedtest.security.service;

import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("Not Found " + username)
        );
        // Member Entity 변화로 인한 변경
        return new MemberDetailsImpl(member);
        // test test test
    }
}