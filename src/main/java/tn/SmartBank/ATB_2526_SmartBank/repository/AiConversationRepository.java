package tn.SmartBank.ATB_2526_SmartBank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.SmartBank.ATB_2526_SmartBank.entity.AiConversation;
import java.util.*;
public interface AiConversationRepository extends JpaRepository<AiConversation, Long> { List<AiConversation> findByUser_IdOrderByUpdatedAtDesc(Long userId); Optional<AiConversation> findByIdAndUser_Id(Long id, Long userId); }
