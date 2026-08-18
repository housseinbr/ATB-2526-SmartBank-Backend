package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatRequest;
import tn.SmartBank.ATB_2526_SmartBank.entity.*;
import tn.SmartBank.ATB_2526_SmartBank.repository.*;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class AiConversationService {
  private final AiConversationRepository conversations; private final AiConversationMessageRepository messages;
  private final UserRepository users; private final AbcenceRepository absences; private final AiGatewayService gateway;
  public List<Map<String,Object>> list(Long userId) { return conversations.findByUser_IdOrderByUpdatedAtDesc(userId).stream().map(c -> Map.<String,Object>of("id",c.getId(),"title",c.getTitle(),"updatedAt",c.getUpdatedAt().toString())).toList(); }
  public List<Map<String,Object>> messages(Long userId, Long id) { conversation(userId,id); return messages.findByConversation_IdOrderByCreatedAtAsc(id).stream().map(m -> Map.<String,Object>of("role",m.getRole(),"content",m.getContent(),"createdAt",m.getCreatedAt().toString())).toList(); }
  public Map<String,Object> chat(Long userId, Long conversationId, String content) {
    AiConversation conversation = conversationId == null ? create(userId, content) : conversation(userId, conversationId);
    save(conversation,"user",content); String response = gateway.chat(new AiChatRequest(content, context(userId), 0.2)).response(); save(conversation,"assistant",response);
    return Map.of("conversationId",conversation.getId(),"response",response);
  }
  private AiConversation create(Long userId,String title) { LocalDateTime now=LocalDateTime.now(); return conversations.save(AiConversation.builder().user(users.getReferenceById(userId)).title(title.substring(0,Math.min(160,title.length()))).createdAt(now).updatedAt(now).build()); }
  private AiConversation conversation(Long userId,Long id) { return conversations.findByIdAndUser_Id(id,userId).orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Conversation introuvable")); }
  private void save(AiConversation c,String role,String content) { messages.save(AiConversationMessage.builder().conversation(c).role(role).content(content).createdAt(LocalDateTime.now()).build()); c.setUpdatedAt(LocalDateTime.now()); conversations.save(c); }
  private String context(Long id) { User u=users.getReferenceById(id); long mine=absences.findByUser_Id(id).size(); long team=u.getRole().name().equals("SUPERVISEUR")?absences.findByUser_Superviseur_Id(id).size():0; long all=u.getRole().name().equals("ADMIN")?absences.count():0; return "Vous êtes l'assistant SmartBank. Données autorisées : rôle="+u.getRole()+", mes absences="+mine+", équipe="+team+", total admin="+all+". Répondez en français. Ne validez ni ne créez aucune donnée; indiquez qu'une confirmation est requise."; }
}
