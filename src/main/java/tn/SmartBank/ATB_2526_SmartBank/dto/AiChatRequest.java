package tn.SmartBank.ATB_2526_SmartBank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public record AiChatRequest(
        @NotBlank @Size(max = 8000) String message,
        @Size(max = 4000) String systemPrompt,
                Double temperature,
                List<Map<String, String>> conversationHistory,
                String sessionId
) {
        public AiChatRequest(String message, String systemPrompt, Double temperature) {
                this(message, systemPrompt, temperature, List.of(), "default");
        }

        public AiChatRequest(String message, String systemPrompt, Double temperature, List<Map<String, String>> conversationHistory) {
                this(message, systemPrompt, temperature, conversationHistory, "default");
        }
}
