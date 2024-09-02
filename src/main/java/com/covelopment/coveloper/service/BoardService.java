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

        // upvoteCount와 downvoteCount를 포함하여 PostDTO 반환
        return new PostDTO(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                member.getName(),
                savedPost.getUpvoteCount(),
                savedPost.getDownvoteCount()
        );
    }

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

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getMember().getName(),
                        post.getUpvoteCount(),
                        post.getDownvoteCount()))
                .collect(Collectors.toList());
    }
}
