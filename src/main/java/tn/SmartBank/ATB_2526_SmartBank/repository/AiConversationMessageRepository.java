package tn.SmartBank.ATB_2526_SmartBank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.SmartBank.ATB_2526_SmartBank.entity.AiConversationMessage;
import java.util.*;
public interface AiConversationMessageRepository extends JpaRepository<AiConversationMessage, Long> { List<AiConversationMessage> findByConversation_IdOrderByCreatedAtAsc(Long conversationId); }
