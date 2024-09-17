package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.*;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.service.BoardService;
import com.covelopment.coveloper.service.MemberService;
import com.covelopment.coveloper.util.ApiConstants;
import com.covelopment.coveloper.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    private Member getAuthenticatedMember(HttpServletRequest request) {
        String token = tokenUtil.extractToken(request);
        String email = tokenUtil.getEmailFromToken(token);
        return memberService.findByEmail(email);
    }

    @PostMapping("/post")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        PostDTO createdPost = boardService.createPost(postDTO, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody PostDTO postDTO, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
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
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        boardService.deletePost(postId, member);
        ApiResponse response = new ApiResponse(ApiConstants.POST_DELETED, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    // QnA 게시판의 답변 채택 기능
    @PostMapping("/post/{postId}/select-answer/{commentId}")
    public ResponseEntity<ApiResponse> selectAnswer(@PathVariable("postId") Long postId, @PathVariable Long commentId, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        boardService.selectAnswer(postId, commentId, member);
        ApiResponse response = new ApiResponse(ApiConstants.ANSWER_SELECTED, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    // 댓글 생성
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable("postId") Long postId, @Valid @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        CommentDTO createdComment = boardService.addComment(postId, commentDTO, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<CommentDTO> comments = boardService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable("commentId") Long commentId, @Valid @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        CommentDTO updatedComment = boardService.updateComment(commentId, commentDTO, member);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        boardService.deleteComment(commentId, member);
        ApiResponse response = new ApiResponse(ApiConstants.COMMENT_DELETED, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    // 게시글 추천/비추천 기능
    @PostMapping("/post/{postId}/vote")
    public ResponseEntity<VoteDTO> voteOnPost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        VoteDTO result = boardService.voteOnPost(postId, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 팀장인 사용자가 댓글 작성자를 팀원으로 추가
    @PostMapping("/post/{postId}/add-member/{commentId}")
    public ResponseEntity<Void> addTeamMember(@PathVariable("postId") Long postId, @PathVariable Long commentId, HttpServletRequest request) {
        Member teamLeader = getAuthenticatedMember(request);  // 팀장 확인
        boardService.addTeamMember(postId, commentId, teamLeader);  // 팀원 추가 로직 호출
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}/team-members")
    public ResponseEntity<List<MemberDTO>> getTeamMembers(@PathVariable("postId") Long postId) {
        List<MemberDTO> teamMembers = boardService.getTeamMembers(postId);
        return ResponseEntity.ok(teamMembers);
    }


    // 특정 팀의 협업 게시판 조회
    @GetMapping("/team/{teamId}/posts")
    public ResponseEntity<List<PostDTO>> getTeamPosts(@PathVariable Long teamId, HttpServletRequest request) {
        Member member = getAuthenticatedMember(request);
        List<PostDTO> posts = boardService.getPostsForSpecificTeam(teamId, member);
        return ResponseEntity.ok(posts);
    }

}
