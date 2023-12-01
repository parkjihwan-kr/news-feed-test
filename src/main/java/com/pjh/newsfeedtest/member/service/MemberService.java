package com.pjh.newsfeedtest.member.service;

import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.dto.MemberResponseDTO;
import com.pjh.newsfeedtest.member.dto.RequestProfileUpdateDto;
import com.pjh.newsfeedtest.member.dto.SignupDto;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    public void signup(SignupDto signupDto) {
        String username = signupDto.getUsername();
        String bcrytPassword = passwordEncoder.encode(signupDto.getPassword());
        String content = signupDto.getContent();

        Optional<Member> checkUsername = memberRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 등록
        Member member = Member.builder()
                .username(username)
                .password(bcrytPassword)
                .content(content)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(RequestProfileUpdateDto updateProfileDto, MemberDetailsImpl memberDetails) {
        Member memberEntity = memberDetails.getMember();
        String loginPassword = memberDetails.getPassword();

        String rawPassword = updateProfileDto.getPassword();
        // 첫번째 입력받은 비밀번호
        String rawPasswordConfirm = updateProfileDto.getPasswordConfirm();
        log.info(rawPassword);
        log.info(rawPasswordConfirm);
        log.info(memberEntity.getPassword());

        // 두 번째 입력받은 비밀번호

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(rawPassword, loginPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않아 수정할 수 없습니다.");
        }

        // 새 비밀번호를 사용하여 비밀번호 업데이트
        String bcrytPassword = passwordEncoder.encode(rawPasswordConfirm);

        memberEntity.changePassword(bcrytPassword);
        memberEntity.changeContent(updateProfileDto.getContent());
        // change함수 활용해서 password, content를 memberEntity에 저장

        memberRepository.save(memberEntity);
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO readMemberInfo(String username) {
        Optional<Member> result = memberRepository.findByUsername(username);
        Member member = result.orElseThrow();
        return modelMapper.map(member, MemberResponseDTO.class);
    }
}
