package tn.SmartBank.ATB_2526_SmartBank.dto;

import java.util.List;

public record AiLeaveRecommendation(
        String recommendation,
        Double confidence,
        String risk,
        List<String> reasons,
        List<String> risks,
        List<String> evidence,
        String ai_explanation
) {
}
