package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.ChatMessageDTO;
import com.covelopment.coveloper.dto.OpenAIRequestDTO;
import com.covelopment.coveloper.dto.OpenAIResponseDTO;
import com.covelopment.coveloper.exception.ChatbotException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class ChatbotService {

    private final RestTemplate restTemplate;
    private final String openAiApiKey;
    private final String openAiApiUrl = "https://api.openai.com/v1/chat/completions";

    public ChatbotService(RestTemplate restTemplate, @Value("${openai.api.key}") String openAiApiKey) {
        this.restTemplate = restTemplate;
        this.openAiApiKey = openAiApiKey;
    }

    public String getChatbotResponse(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        // 요청 바디 생성
        OpenAIRequestDTO requestBody = new OpenAIRequestDTO();
        requestBody.setModel("gpt-3.5-turbo");
        ChatMessageDTO userMessage = new ChatMessageDTO("user", message);
        requestBody.setMessages(Collections.singletonList(userMessage));

        HttpEntity<OpenAIRequestDTO> request = new HttpEntity<>(requestBody, headers);

        try {
            OpenAIResponseDTO response = restTemplate.postForObject(openAiApiUrl, request, OpenAIResponseDTO.class);

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                ChatMessageDTO assistantMessage = response.getChoices().get(0).getMessage();
                return assistantMessage.getContent().trim();
            }

            throw new ChatbotException("GPT API로부터 응답을 받지 못했습니다.");

        } catch (Exception e) {
            throw new ChatbotException("챗봇 서비스 중 오류가 발생했습니다.", e);
        }
    }
}
