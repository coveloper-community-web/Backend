package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.CommentDTO;
import com.covelopment.coveloper.dto.PostDTO;
import com.covelopment.coveloper.dto.VoteDTO;
import com.covelopment.coveloper.entity.*;
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

    private void handleRecruitmentFields(PostDTO postDTO, Post post) {
        if (post.getBoardType() == BoardType.RECRUITMENT) {
            post.setProjectType(postDTO.getProjectType());
            post.setTeamSize(postDTO.getTeamSize());
            post.setCurrentMembers(postDTO.getCurrentMembers());
        }
    }

    @Transactional
    public PostDTO createPost(PostDTO postDTO, Member member) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setMember(member);  // 글 작성자
        post.setBoardType(postDTO.getBoardType());

        // 구인 게시판일 때만 작성자를 팀장으로 추가
        if (post.getBoardType() == BoardType.RECRUITMENT) {
            post.addTeamLeader(member);  // 작성자를 팀장으로 설정하고 팀원으로 추가
        }

        handleRecruitmentFields(postDTO, post);  // 구인 게시판 전용 필드 설정

        Post savedPost = postRepository.save(post);

        return new PostDTO(savedPost.getId(), savedPost.getTitle(), savedPost.getContent(),
                member.getNickname(), savedPost.getUpvoteCount(), savedPost.getCreatedAt(),
                savedPost.getUpdatedAt(), savedPost.getBoardType(),
                savedPost.getProjectType(), savedPost.getTeamSize(), savedPost.getCurrentMembers(),
                post.getMember().getName());  // teamLeaderName 필드 추가
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

        handleRecruitmentFields(postDTO, post);

        Post updatedPost = postRepository.save(post);

        return new PostDTO(updatedPost.getId(), updatedPost.getTitle(), updatedPost.getContent(),
                member.getNickname(), updatedPost.getUpvoteCount(), updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt(), updatedPost.getBoardType(),
                updatedPost.getProjectType(), updatedPost.getTeamSize(), updatedPost.getCurrentMembers(),
                post.getMember().getName());  // teamLeaderName 필드 추가
    }

    @Transactional(readOnly = true)
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        return new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                post.getMember().getNickname(), post.getUpvoteCount(),
                post.getCreatedAt(), post.getUpdatedAt(),
                post.getBoardType(), post.getProjectType(),
                post.getTeamSize(), post.getCurrentMembers(),
                post.getMember().getName());  // teamLeaderName 필드 추가
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        post.getMember().getNickname(), post.getUpvoteCount(),
                        post.getCreatedAt(), post.getUpdatedAt(),
                        post.getBoardType(), post.getProjectType(),
                        post.getTeamSize(), post.getCurrentMembers(),
                        post.getMember().getName()))  // teamLeaderName 필드 추가
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

        post.getComments().stream()
                .filter(Comment::isSelected)
                .findAny()
                .ifPresent(c -> {
                    throw new IllegalArgumentException("An answer has already been selected.");
                });

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        comment.setSelected(true);
        commentRepository.save(comment);
    }

    @Transactional
    public CommentDTO addComment(Long postId, CommentDTO commentDTO, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        comment.setMember(member);
        comment.setSelected(false);

        Comment savedComment = commentRepository.save(comment);

        return new CommentDTO(savedComment.getId(), savedComment.getContent(),
                member.getNickname(), post.getId(),
                savedComment.getCreatedAt(), savedComment.getUpdatedAt(),
                savedComment.isSelected());
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        return post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(comment -> new CommentDTO(comment.getId(), comment.getContent(),
                        comment.getMember().getNickname(), postId,
                        comment.getCreatedAt(), comment.getUpdatedAt(),
                        comment.isSelected()))
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

        Comment updatedComment = commentRepository.save(comment);

        return new CommentDTO(updatedComment.getId(), updatedComment.getContent(),
                member.getNickname(), updatedComment.getPost().getId(),
                updatedComment.getCreatedAt(), updatedComment.getUpdatedAt(),
                updatedComment.isSelected());
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
            voteRepository.delete(existingVote.get());
            post.setUpvoteCount(post.getUpvoteCount() - 1);
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setMember(member);
            voteRepository.save(vote);
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }

        postRepository.save(post);

        return new VoteDTO(post.getId(), post.getUpvoteCount());
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByMember(Member member) {
        return postRepository.findByMember(member).stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        post.getMember().getNickname(), post.getUpvoteCount(),
                        post.getCreatedAt(), post.getUpdatedAt(),
                        post.getBoardType(), post.getProjectType(),
                        post.getTeamSize(), post.getCurrentMembers(),
                        post.getMember().getName()))  // teamLeaderName 필드 추가
                .collect(Collectors.toList());
    }

    // Team

    @Transactional
    public void addTeamMember(Long postId, Long commentId, Member teamLeader) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 구인 게시판에서만 팀원을 추가 가능
        if (post.getBoardType() != BoardType.RECRUITMENT) {
            throw new IllegalArgumentException("Team members can only be added in recruitment posts.");
        }

        // 팀장 여부 확인
        if (!post.getTeamLeader().equals(teamLeader)) {
            throw new IllegalArgumentException("Only the team leader can add members.");
        }

        // 댓글 작성자 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        Member newTeamMember = comment.getMember();  // 댓글 작성자를 팀원으로 추가
        post.addTeamMember(newTeamMember);  // 팀원 추가
        postRepository.save(post);  // 업데이트된 게시글 저장
    }

    // 사용자가 속한 팀 목록 반환
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByMemberTeams(Member member) {
        List<Post> teamPosts = postRepository.findByTeamMembersContaining(member);
        return teamPosts.stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        post.getMember().getNickname(), post.getUpvoteCount(),
                        post.getCreatedAt(), post.getUpdatedAt(),
                        post.getBoardType(), post.getProjectType(),
                        post.getTeamSize(), post.getCurrentMembers(),
                        post.getMember().getName()))  // teamLeaderName 필드 추가
                .collect(Collectors.toList());
    }

    // 특정 팀 게시판 조회
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsForSpecificTeam(Long teamId, Member member) {
        Post teamPost = postRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));

        if (!teamPost.getTeamMembers().contains(member)) {
            throw new IllegalArgumentException("You are not a member of this team.");
        }

        return postRepository.findByTeamMembersContaining(member).stream()
                .filter(post -> post.getId().equals(teamId))
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        post.getMember().getNickname(), post.getUpvoteCount(),
                        post.getCreatedAt(), post.getUpdatedAt(),
                        post.getBoardType(), post.getProjectType(),
                        post.getTeamSize(), post.getCurrentMembers(),
                        post.getMember().getName()))  // teamLeaderName 필드 추가
                .collect(Collectors.toList());
    }
}
