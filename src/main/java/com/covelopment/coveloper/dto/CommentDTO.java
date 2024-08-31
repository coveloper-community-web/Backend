package com.covelopment.coveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private String authorName; // Comment 작성자 이름
    private Long postId; // Comment가 달린 Post의 ID
}
