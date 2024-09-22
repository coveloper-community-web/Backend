package com.covelopment.coveloper.exception;

public class ChatbotException extends RuntimeException {
    public ChatbotException(String message) {
        super(message);
    }

    public ChatbotException(String message, Throwable cause) {
        super(message, cause);
    }
}
