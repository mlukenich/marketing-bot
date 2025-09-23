package com.marketer.affiliate_agent.dto.openai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiRequest {
    private String model;
    private List<Message> messages;

    public OpenAiRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(new Message("user", prompt));
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
