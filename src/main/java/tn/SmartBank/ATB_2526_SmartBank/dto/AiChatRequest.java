package tn.SmartBank.ATB_2526_SmartBank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiChatRequest(
        @NotBlank @Size(max = 8000) String message,
        @Size(max = 4000) String systemPrompt,
        Double temperature
) {
}
