package com.pjh.newsfeedtest.auth;

import com.pjh.newsfeedtest.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    private final MemberService memberService;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage(){
        return "signup";
    }

    @GetMapping("/")
    public String mainPage(){
        return "mainpage";

    }
}
