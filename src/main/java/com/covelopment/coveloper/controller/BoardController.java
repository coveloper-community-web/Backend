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

    @PutMapping("/post/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDTO, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        PostDTO updatedPost = boardService.updatePost(postId, postDTO, member);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("postId") Long postId) {
        PostDTO post = boardService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(boardService.getAllPosts());
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        boardService.deletePost(postId, member);
        return ResponseEntity.noContent().build();  // 204 No Content로 응답
    }

    // QnA 게시판의 답변 채택 기능
    @PostMapping("/post/{postId}/select-answer/{commentId}")
    public ResponseEntity<Void> selectAnswer(@PathVariable Long postId, @PathVariable Long commentId, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        boardService.selectAnswer(postId, commentId, member);
        return ResponseEntity.ok().build();
    }

    // 댓글 기능
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        CommentDTO createdComment = boardService.addComment(postId, commentDTO, member);
        return ResponseEntity.status(201).body(createdComment);
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<CommentDTO> comments = boardService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }


    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        CommentDTO updatedComment = boardService.updateComment(commentId, commentDTO, member);
        return ResponseEntity.ok(updatedComment);
    }


    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        boardService.deleteComment(commentId, member);
        return ResponseEntity.noContent().build();
    }

    // 게시글 추천/비추천 기능
    @PostMapping("/post/{postId}/vote")
    public ResponseEntity<VoteDTO> voteOnPost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        Member member = memberService.findByEmail(email);
        VoteDTO result = boardService.voteOnPost(postId, member);
        return ResponseEntity.status(201).body(result);  // 201 Created로 응답
    }


}
