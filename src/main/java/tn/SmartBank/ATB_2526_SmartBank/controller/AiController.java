package tn.SmartBank.ATB_2526_SmartBank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiLeaveRecommendation;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.AbsenceService;
import tn.SmartBank.ATB_2526_SmartBank.service.AiGatewayService;

import java.util.Map;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
public class AiController {

    private final AiGatewayService aiGatewayService;
    private final AbsenceService absenceService;
    private final tn.SmartBank.ATB_2526_SmartBank.service.AiConversationService conversationService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiGatewayService.chat(request));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<?, ?>> health() {
        return ResponseEntity.ok(aiGatewayService.health());
    }

    @PostMapping("/leaves/{absenceId}/recommendation")
    public ResponseEntity<AiLeaveRecommendation> leaveRecommendation(@PathVariable Long absenceId, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(aiGatewayService.leaveRecommendation(absenceService.buildAiContext(absenceId, user.getId())));
    }

    @GetMapping("/conversations") public ResponseEntity<List<Map<String,Object>>> conversations(Authentication authentication) { return ResponseEntity.ok(conversationService.list(((UserDetailsImpl) authentication.getPrincipal()).getId())); }
    @GetMapping("/conversations/{id}/messages") public ResponseEntity<List<Map<String,Object>>> messages(@PathVariable Long id, Authentication authentication) { return ResponseEntity.ok(conversationService.messages(((UserDetailsImpl) authentication.getPrincipal()).getId(), id)); }
    @PostMapping("/conversations/chat") public ResponseEntity<Map<String,Object>> conversationChat(@RequestBody Map<String,Object> body, Authentication authentication) { Long id=body.get("conversationId") instanceof Number n?n.longValue():null; String message=String.valueOf(body.getOrDefault("message", "")).trim(); if(message.isBlank()) throw new IllegalArgumentException("Message obligatoire"); return ResponseEntity.ok(conversationService.chat(((UserDetailsImpl) authentication.getPrincipal()).getId(),id,message)); }
}
