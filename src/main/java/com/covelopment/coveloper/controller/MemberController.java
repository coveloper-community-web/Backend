package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.dto.PostDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.BoardService;
import com.covelopment.coveloper.service.MemberService;
import com.covelopment.coveloper.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final TokenUtil tokenUtil;

    public MemberController(MemberService memberService, BoardService boardService, TokenUtil tokenUtil) {
        this.memberService = memberService;
        this.boardService = boardService;
        this.tokenUtil = tokenUtil;
    }
    private Member getAuthenticatedMember(HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        return memberService.findByEmail(email);
    }

    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@Validated @RequestBody MemberDTO memberDTO) {
        Member registeredMember = memberService.registerMember(memberDTO);
        return ResponseEntity.ok(registeredMember);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO, HttpServletResponse response) {
        String token = memberService.login(memberDTO.getEmail(), memberDTO.getPassword());
        response.setHeader("Authorization", "Bearer " + token);

        return ResponseEntity.ok(token);
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

    // 특정 회원의 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getMyPosts(HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        List<PostDTO> posts = boardService.getPostsByMember(member);
        return ResponseEntity.ok(posts);
    }

    // 팀 목록 조회 API (사용자가 속한 팀 목록을 반환)
    @GetMapping("/teams")
    public ResponseEntity<List<PostDTO>> getMyTeams(HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        List<PostDTO> teamPosts = boardService.getPostsByMemberTeams(member);
        return ResponseEntity.ok(teamPosts);
    }


}
