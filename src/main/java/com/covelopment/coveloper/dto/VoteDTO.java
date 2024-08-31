package com.covelopment.coveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {
    private Long id;
    private boolean isUpvote;
    private Long postId; // Vote가 달린 Post의 ID
}
