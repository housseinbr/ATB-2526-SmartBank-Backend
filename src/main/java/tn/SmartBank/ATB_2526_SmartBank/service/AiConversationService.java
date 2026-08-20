package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiChatResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.AiActionConfirmRequest;
import tn.SmartBank.ATB_2526_SmartBank.entity.*;
import tn.SmartBank.ATB_2526_SmartBank.repository.*;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class AiConversationService {
  private final AiConversationRepository conversations; private final AiConversationMessageRepository messages;
  private final UserRepository users; private final AbcenceRepository absences; private final AiGatewayService gateway;
  public List<Map<String,Object>> list(Long userId) { return conversations.findByUser_IdOrderByUpdatedAtDesc(userId).stream().map(c -> Map.<String,Object>of("id",c.getId(),"title",c.getTitle(),"updatedAt",c.getUpdatedAt().toString())).toList(); }
  @Transactional(readOnly = true)
  public List<Map<String,Object>> listForAdmin(Long userId) { return list(userId); }
  public List<Map<String,Object>> messages(Long userId, Long id) { conversation(userId,id); return messages.findByConversation_IdOrderByCreatedAtAsc(id).stream().map(m -> Map.<String,Object>of("role",m.getRole(),"content",m.getContent(),"createdAt",m.getCreatedAt().toString())).toList(); }
  public AiChatResponse directChat(Long userId, AiChatRequest request) {
    String prompt = context(userId) + "\nRépondez directement aux questions sur l'utilisateur authentifié. Si l'utilisateur demande qui il est, donnez son nom et son rôle connus dans ce contexte.";
    return gateway.chat(new AiChatRequest(request.message(), prompt, request.temperature(), request.conversationHistory(), request.sessionId()));
  }
  public void delete(Long userId, Long id) {
    AiConversation conversation = conversation(userId, id);
    messages.deleteAll(messages.findByConversation_IdOrderByCreatedAtAsc(id));
    conversations.delete(conversation);
  }
  public Map<String,Object> chat(Long userId, Long conversationId, String content) {
    AiConversation conversation = conversationId == null ? create(userId, content) : conversation(userId, conversationId);
    List<Map<String, String>> history = messages.findByConversation_IdOrderByCreatedAtAsc(conversation.getId()).stream()
        .skip(Math.max(0, messages.findByConversation_IdOrderByCreatedAtAsc(conversation.getId()).size() - 12L))
        .map(item -> Map.of("role", item.getRole(), "content", item.getContent()))
        .toList();
    save(conversation,"user",content);
    var aiResponse = isConfirmation(content)
      ? gateway.confirmAction(new AiActionConfirmRequest(String.valueOf(conversation.getId()), content))
      : gateway.chat(new AiChatRequest(content, context(userId), 0.2, history, String.valueOf(conversation.getId())));
    String response = aiResponse.response();
    save(conversation,"assistant",response);
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("conversationId", conversation.getId());
    result.put("response", response);
    result.put("state", aiResponse.state());
    result.put("intent", aiResponse.intent());
    result.put("action", aiResponse.action());
    return result;
  }
  private boolean isConfirmation(String content) {
    return content.trim().toLowerCase().matches("^(oui|yes|confirm|confirmed|confirme|confirmer|je confirme|oui je confirme)( la demande)?$");
  }
  private AiConversation create(Long userId,String title) { LocalDateTime now=LocalDateTime.now(); return conversations.save(AiConversation.builder().user(users.getReferenceById(userId)).title(title.substring(0,Math.min(160,title.length()))).createdAt(now).updatedAt(now).build()); }
  private AiConversation conversation(Long userId,Long id) { return conversations.findByIdAndUser_Id(id,userId).orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Conversation introuvable")); }
  private void save(AiConversation c,String role,String content) { messages.save(AiConversationMessage.builder().conversation(c).role(role).content(content).createdAt(LocalDateTime.now()).build()); c.setUpdatedAt(LocalDateTime.now()); conversations.save(c); }
  private String context(Long id) {
    User user = users.findById(id).orElseThrow();
    String identity = displayName(user);
    List<Abcence> myAbsenceList = absences.findByUser_Id(id);
    StringBuilder context = new StringBuilder("Vous êtes l'assistant SmartBank. Répondez en français.\n")
        .append("Utilisateur authentifié : ").append(identity).append(" (rôle ").append(user.getRole()).append(").\n")
        .append("Vous pouvez utiliser cette identité pour répondre à 'qui suis-je', sans demander à l'utilisateur de se présenter.\n")
        .append("Si l'utilisateur demande son identité, répondez directement avec son nom et son rôle.\n");
    if (user.getSuperviseur() != null) {
      context.append("Superviseur de l'utilisateur : ").append(displayName(user.getSuperviseur())).append(".\n");
    }
    if ("SUPERVISEUR".equals(user.getRole().name())) {
      List<User> team = absences.findByUser_Superviseur_Id(id).stream()
          .map(item -> item.getUser())
          .filter(Objects::nonNull)
          .distinct()
          .toList();
      context.append("Équipe supervisée : ").append(team.stream().map(this::displayName).distinct().toList()).append(".\n")
          .append("Nombre de collaborateurs connus dans les demandes : ").append(team.size()).append(".\n");
    }
    if ("ADMIN".equals(user.getRole().name())) {
      context.append("Rôle administrateur : accès aux informations globales selon les autorisations backend.\n");
    }
    context.append("Mes absences enregistrées : ").append(myAbsenceList.size()).append(".\n");
    if (!myAbsenceList.isEmpty()) {
      context.append("Détails de mes absences autorisées :\n");
      myAbsenceList.stream().limit(20).forEach(absence -> context
        .append("- ID ").append(absence.getIdAbcance())
        .append(" | type=").append(absence.getType())
        .append(" | début=").append(absence.getDateStart())
        .append(" | fin=").append(absence.getDateEnd())
        .append(" | statut=").append(absence.getStatus())
        .append(" | commentaire=").append(absence.getComment() == null || absence.getComment().isBlank() ? "aucun" : absence.getComment())
        .append("\n"));
    }
    return context
        .append("Ne validez ni ne créez aucune donnée vous-même; une confirmation humaine et le backend sont requis.")
        .toString();
  }
  private String displayName(User user) {
    String name = ((user.getFirstName() == null ? "" : user.getFirstName()) + " " + (user.getLastName() == null ? "" : user.getLastName())).trim();
    return name.isBlank() ? user.getEmail() : name;
  }
}
