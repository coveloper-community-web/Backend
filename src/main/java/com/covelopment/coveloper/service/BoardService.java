package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.CommentDTO;
import com.covelopment.coveloper.dto.PostDTO;
import com.covelopment.coveloper.dto.VoteDTO;
import com.covelopment.coveloper.entity.BoardType;
import com.covelopment.coveloper.entity.Comment;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.entity.Post;
import com.covelopment.coveloper.entity.Vote;
import com.covelopment.coveloper.repository.CommentRepository;
import com.covelopment.coveloper.repository.PostRepository;
import com.covelopment.coveloper.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
        post.setBoardType(postDTO.getBoardType());

        // 구인 게시판의 경우 추가 필드 설정
        if (post.getBoardType() == BoardType.RECRUITMENT) {
            post.setProjectType(postDTO.getProjectType());
            post.setTeamSize(postDTO.getTeamSize());
            post.setCurrentMembers(postDTO.getCurrentMembers());
        }

        Post savedPost = postRepository.save(post);

        return new PostDTO(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                member.getNickname(),
                savedPost.getUpvoteCount(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt(),
                savedPost.getBoardType(),
                savedPost.getProjectType(),
                savedPost.getTeamSize(),
                savedPost.getCurrentMembers()
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

        // 구인 게시판의 경우 추가 필드 업데이트
        if (post.getBoardType() == BoardType.RECRUITMENT) {
            post.setProjectType(postDTO.getProjectType());
            post.setTeamSize(postDTO.getTeamSize());
            post.setCurrentMembers(postDTO.getCurrentMembers());
        }

        Post updatedPost = postRepository.save(post);

        return new PostDTO(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                member.getNickname(),
                updatedPost.getUpvoteCount(),
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt(),
                updatedPost.getBoardType(),
                updatedPost.getProjectType(),
                updatedPost.getTeamSize(),
                updatedPost.getCurrentMembers()
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
                post.getUpdatedAt(),
                post.getBoardType(),
                post.getProjectType(),
                post.getTeamSize(),
                post.getCurrentMembers()
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
                        post.getUpdatedAt(),
                        post.getBoardType(),
                        post.getProjectType(),
                        post.getTeamSize(),
                        post.getCurrentMembers()
                ))
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

    // BoardService.java
    @Transactional
    public void selectAnswer(Long postId, Long commentId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        if (post.getBoardType() != BoardType.QNA) {
            throw new IllegalArgumentException("Only QnA posts can have selected answers.");
        }

        if (!post.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Only the post author can select an answer.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        // 기존에 채택된 답변이 있는지 확인 (이미 채택된 답변이 있으면 오류 처리)
        List<Comment> comments = post.getComments();
        for (Comment c : comments) {
            if (c.isSelected()) {
                throw new IllegalArgumentException("An answer has already been selected.");
            }
        }

        // 답변을 채택
        comment.setSelected(true);
        commentRepository.save(comment);  // 채택된 답변 저장
    }


    // 댓글 생성, 조회, 수정, 삭제 로직 (이전과 동일)
    @Transactional
    public CommentDTO addComment(Long postId, CommentDTO commentDTO, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        comment.setMember(member);
        Comment savedComment = commentRepository.save(comment);

        return new CommentDTO(savedComment.getId(), savedComment.getContent(), member.getNickname(), post.getId(), savedComment.getCreatedAt(), savedComment.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        return post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())  // 최신순 정렬
                .map(comment -> new CommentDTO(comment.getId(), comment.getContent(), comment.getMember().getNickname(), postId, comment.getCreatedAt(), comment.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized access.");
        }
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return new CommentDTO(comment.getId(), comment.getContent(), member.getNickname(), comment.getPost().getId(), comment.getCreatedAt(), comment.getUpdatedAt());
    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized access.");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public VoteDTO voteOnPost(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        Optional<Vote> existingVote = voteRepository.findByPostAndMember(post, member);

        if (existingVote.isPresent()) {
            // 이미 투표한 경우 투표 취소
            voteRepository.delete(existingVote.get());
            post.setUpvoteCount(post.getUpvoteCount() - 1);
        } else {
            // 새로운 투표 추가
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setMember(member);
            voteRepository.save(vote);
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }

        postRepository.save(post);  // 변경된 추천 수 저장

        return new VoteDTO(post.getId(), post.getUpvoteCount());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByMember(Member member) {
        return postRepository.findByMember(member).stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getMember().getNickname(),
                        post.getUpvoteCount(),
                        post.getCreatedAt(),
                        post.getUpdatedAt(),
                        post.getBoardType(),
                        post.getProjectType(),
                        post.getTeamSize(),
                        post.getCurrentMembers()
                ))
                .collect(Collectors.toList());
    }
}
