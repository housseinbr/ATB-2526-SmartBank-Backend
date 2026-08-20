package tn.SmartBank.ATB_2526_SmartBank.dto;

import jakarta.validation.constraints.NotBlank;

public record AiActionConfirmRequest(
        @NotBlank String sessionId,
        @NotBlank String confirmation
) {
}