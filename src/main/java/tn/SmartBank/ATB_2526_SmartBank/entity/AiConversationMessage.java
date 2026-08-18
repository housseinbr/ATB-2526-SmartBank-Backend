package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_conversation_message")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiConversationMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "conversation_id", nullable = false) private AiConversation conversation;
    @Column(nullable = false, length = 16) private String role;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Column(nullable = false) private LocalDateTime createdAt;
}
