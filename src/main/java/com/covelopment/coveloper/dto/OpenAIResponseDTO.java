package com.covelopment.coveloper.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIResponseDTO {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private ChatMessageDTO message;
        private String finish_reason;
    }
}
