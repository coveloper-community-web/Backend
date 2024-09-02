// MemberController.java
package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.MemberService;
import com.covelopment.coveloper.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final TokenUtil tokenUtil;

    public MemberController(MemberService memberService, TokenUtil tokenUtil) {
        this.memberService = memberService;
        this.tokenUtil = tokenUtil;
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

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            responseBody.put("user", memberService.findByEmail(memberDTO.getEmail()));

            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Member> getProfile(HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);

        if (!tokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        return ResponseEntity.ok(member);
    }

}
