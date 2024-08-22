package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
            String token = memberService.login(memberDTO.getEmail(), memberDTO.getPassword());
            response.setHeader("Authorization", "Bearer " + token);
            System.out.println(token);
            return ResponseEntity.ok("Login successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<Member> getProfile(@RequestHeader("Authorization") String token) {
        System.out.println("getProfile 실행");
        if (!memberService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = memberService.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        System.out.println(member);
        return ResponseEntity.ok(member);
    }
}
