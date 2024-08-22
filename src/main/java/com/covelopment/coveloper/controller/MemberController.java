package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@Validated @RequestBody MemberDTO memberDTO) {
        Member registeredMember = memberService.registerMember(memberDTO);
        return ResponseEntity.ok(registeredMember);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO, HttpServletResponse response) {
        try {
            String token = memberService.authenticate(memberDTO.getEmail(), memberDTO.getPassword());
            response.setHeader("Authorization", "Bearer " + token);
            return ResponseEntity.ok("Login successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
