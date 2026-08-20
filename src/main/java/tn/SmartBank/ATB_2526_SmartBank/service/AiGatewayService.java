package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiActionConfirmRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiLeaveRecommendation;
import tn.SmartBank.ATB_2526_SmartBank.exception.AiServiceUnavailableException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiGatewayService {

    private final RestClient.Builder restClientBuilder;

    @Value("${ai.service.base-url}")
    private String aiServiceBaseUrl;

    public AiChatResponse chat(AiChatRequest request) {
        try {
            AiChatResponse response = client().post()
                    .uri("/api/ai/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiChatResponse.class);
            if (response == null || response.response() == null) {
                throw new IllegalStateException("Le service IA a retourné une réponse vide");
            }
            return response;
        } catch (RestClientException exception) {
            throw new AiServiceUnavailableException("Le service IA local est indisponible", exception);
        }
    }

    public AiChatResponse confirmAction(AiActionConfirmRequest request) {
        try {
            AiChatResponse response = client().post()
                    .uri("/api/ai/action/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiChatResponse.class);
            if (response == null || response.response() == null) {
                throw new IllegalStateException("Le service IA a retourné une réponse vide");
            }
            return response;
        } catch (RestClientException exception) {
            throw new AiServiceUnavailableException("Le service IA local est indisponible", exception);
        }
    }

    public Map<?, ?> health() {
        try {
            Map<?, ?> response = client().get()
                    .uri("/health/ready")
                    .retrieve()
                    .body(Map.class);
            return response == null ? Map.of("status", "unknown") : response;
        } catch (RestClientException exception) {
            throw new AiServiceUnavailableException("Le service IA local est indisponible", exception);
        }
    }

    public AiLeaveRecommendation leaveRecommendation(Map<String, Object> context) {
        try {
            return client().post()
                    .uri("/api/ai/leave/recommendation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(context)
                    .retrieve()
                    .body(AiLeaveRecommendation.class);
        } catch (RestClientException exception) {
            throw new AiServiceUnavailableException("Le service IA local est indisponible", exception);
        }
    }

    private RestClient client() {
        return restClientBuilder.baseUrl(aiServiceBaseUrl).build();
    }
}
