package com.covelopment.coveloper.dto;

import com.covelopment.coveloper.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private int upvoteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BoardType boardType;  // 게시판 유형

    // 구인 게시판 전용 필드
    private String projectType;
    private int teamSize;
    private int currentMembers;
}
