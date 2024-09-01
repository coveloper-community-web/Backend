package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.CommentDTO;
import com.covelopment.coveloper.dto.PostDTO;
import com.covelopment.coveloper.dto.VoteDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.BoardService;
import com.covelopment.coveloper.service.MemberService;
import com.covelopment.coveloper.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final TokenUtil tokenUtil;

    public BoardController(BoardService boardService, MemberService memberService, TokenUtil tokenUtil) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/post")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        PostDTO createdPost = boardService.createPost(postDTO, member);
        return ResponseEntity.status(201).body(createdPost);  // 201 Created로 응답
    }

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        CommentDTO createdComment = boardService.addComment(postId, commentDTO, member);
        return ResponseEntity.status(201).body(createdComment);  // 201 Created로 응답
    }

    @PostMapping("/post/{postId}/vote")
    public ResponseEntity<VoteDTO> voteOnPost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        VoteDTO result = boardService.voteOnPost(postId, member);
        return ResponseEntity.status(201).body(result);  // 201 Created로 응답
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(boardService.getAllPosts());
    }
}
