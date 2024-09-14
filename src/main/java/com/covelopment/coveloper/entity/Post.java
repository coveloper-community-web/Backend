package com.covelopment.coveloper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_leader_id", nullable = false)
    private Member teamLeader;  // 팀장 (글 작성자)

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    private int upvoteCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;  // 게시판 유형

    // 구인 게시판 전용 필드
    private String projectType;
    private int teamSize;
    private int currentMembers;

    // 팀원 필드 추가 (Many-to-Many 관계 설정)
    @ManyToMany
    @JoinTable(
            name = "post_team_members",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> teamMembers = new ArrayList<>();  // 팀원 목록

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 글 작성 시 작성자를 팀장과 팀원으로 추가
    public void addTeamLeader(Member teamLeader) {
        if (!teamMembers.contains(teamLeader)) {
            this.teamLeader = teamLeader;
            teamMembers.add(teamLeader);  // 팀장도 팀원으로 추가
        }
    }

    // 팀원 추가 메서드
    public void addTeamMember(Member member) {
        if (!teamMembers.contains(member)) {
            teamMembers.add(member);
            currentMembers++;  // 현재 팀원 수 증가
        }
    }

    // 팀원 목록 반환 메서드
    public List<Member> getTeamMembers() {
        return teamMembers;
    }
}
