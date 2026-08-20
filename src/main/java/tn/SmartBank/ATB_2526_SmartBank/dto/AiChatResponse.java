package tn.SmartBank.ATB_2526_SmartBank.dto;

import java.util.Map;

public record AiChatResponse(String model, String response, String state, String intent, Map<String, Object> action) {
}
