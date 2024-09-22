package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.ChatRequestDTO;
import com.covelopment.coveloper.dto.ChatResponseDTO;
import com.covelopment.coveloper.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/talk")
    public ResponseEntity<ChatResponseDTO> talkToChatbot(@RequestBody ChatRequestDTO chatRequestDTO) {
        String response = chatbotService.getChatbotResponse(chatRequestDTO.getMessage());
        ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setResponse(response);
        return ResponseEntity.ok(chatResponseDTO);
    }
}
