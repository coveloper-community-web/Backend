package com.covelopment.coveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private String authorName;
    private Long postId;
    private LocalDateTime createdAt;  // 생성일시
    private LocalDateTime updatedAt;  // 수정일시 (수정용)
    private boolean selected;  // 채택 여부 필드 추가
}
