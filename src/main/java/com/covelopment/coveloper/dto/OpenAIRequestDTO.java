package com.covelopment.coveloper.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIRequestDTO {
    private String model;
    private List<ChatMessageDTO> messages;
}
