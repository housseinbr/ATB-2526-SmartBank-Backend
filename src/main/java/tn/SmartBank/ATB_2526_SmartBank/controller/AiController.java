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
import tn.SmartBank.ATB_2526_SmartBank.dto.AiActionConfirmRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiLeaveRecommendation;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.AbsenceService;
import tn.SmartBank.ATB_2526_SmartBank.service.AiGatewayService;

import java.util.Map;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class AiController {

    private final AiGatewayService aiGatewayService;
    private final AbsenceService absenceService;
    private final tn.SmartBank.ATB_2526_SmartBank.service.AiConversationService conversationService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(conversationService.directChat(user.getId(), request));
    }

    @PostMapping("/action/confirm")
    public ResponseEntity<AiChatResponse> confirmAction(@Valid @RequestBody AiActionConfirmRequest request) {
        return ResponseEntity.ok(aiGatewayService.confirmAction(request));
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
    @GetMapping("/admin/users/{userId}/conversations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String,Object>>> adminConversations(@PathVariable Long userId) { return ResponseEntity.ok(conversationService.listForAdmin(userId)); }
    @GetMapping("/conversations/{id}/messages") public ResponseEntity<List<Map<String,Object>>> messages(@PathVariable Long id, Authentication authentication) { return ResponseEntity.ok(conversationService.messages(((UserDetailsImpl) authentication.getPrincipal()).getId(), id)); }
    @DeleteMapping("/conversations/{id}") public ResponseEntity<Void> deleteConversation(@PathVariable Long id, Authentication authentication) { conversationService.delete(((UserDetailsImpl) authentication.getPrincipal()).getId(), id); return ResponseEntity.noContent().build(); }
    @PostMapping("/conversations/chat") public ResponseEntity<Map<String,Object>> conversationChat(@RequestBody Map<String,Object> body, Authentication authentication) { Long id=body.get("conversationId") instanceof Number n?n.longValue():null; String message=String.valueOf(body.getOrDefault("message", "")).trim(); if(message.isBlank()) throw new IllegalArgumentException("Message obligatoire"); return ResponseEntity.ok(conversationService.chat(((UserDetailsImpl) authentication.getPrincipal()).getId(),id,message)); }
}
