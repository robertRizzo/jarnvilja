package com.jarnvilja.controller;


import com.jarnvilja.model.Role;
import com.jarnvilja.model.User;
import com.jarnvilja.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.jarnvilja.model.Role.ROLE_MEMBER;

@Controller
public class RegistrationController {
    private final MemberService memberService;

    @Autowired
    public RegistrationController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole(ROLE_MEMBER);

        memberService.registerUser (user);

        return "redirect:/login?success";
    }
}
