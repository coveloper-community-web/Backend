package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.CommentDTO;
import com.covelopment.coveloper.dto.PostDTO;
import com.covelopment.coveloper.dto.VoteDTO;
import com.covelopment.coveloper.entity.Comment;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.entity.Post;
import com.covelopment.coveloper.entity.Vote;
import com.covelopment.coveloper.repository.CommentRepository;
import com.covelopment.coveloper.repository.PostRepository;
import com.covelopment.coveloper.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    public BoardService(PostRepository postRepository, CommentRepository commentRepository, VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public PostDTO createPost(PostDTO postDTO, Member member) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setMember(member);
        Post savedPost = postRepository.save(post);

        return new PostDTO(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                member.getName(),
                savedPost.getUpvoteCount(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );
    }

    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        if (!post.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("You can only update your own posts");
        }

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        Post updatedPost = postRepository.save(post);

        return new PostDTO(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                member.getNickname(),
                updatedPost.getUpvoteCount(),
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt()
        );
    }

    @Transactional
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getNickname(),
                post.getUpvoteCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getMember().getNickname(),
                        post.getUpvoteCount(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()))
                .collect(Collectors.toList());
    }
    @Transactional
    public void deletePost(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        if (!post.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }
    // 댓글 생성
    @Transactional
    public CommentDTO addComment(Long postId, CommentDTO commentDTO, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        comment.setMember(member);
        Comment savedComment = commentRepository.save(comment);
        return new CommentDTO(savedComment.getId(), savedComment.getContent(), member.getName(), post.getId());
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        return post.getComments().stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getContent(), comment.getMember().getName(), postId))
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return new CommentDTO(comment.getId(), comment.getContent(), member.getName(), comment.getPost().getId());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public VoteDTO voteOnPost(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        Optional<Vote> existingVote = voteRepository.findByPostAndMember(post, member);

        if (existingVote.isPresent()) {
            // 사용자가 이미 투표한 경우 -> 투표 취소
            voteRepository.delete(existingVote.get());
            post.setUpvoteCount(post.getUpvoteCount() - 1);
        } else {
            // 사용자가 투표하지 않은 경우 -> 새로운 투표 추가
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setMember(member);
            voteRepository.save(vote);
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }

        postRepository.save(post);  // 변경된 추천 수를 저장

        return new VoteDTO(post.getId(), post.getUpvoteCount());
    }
}
