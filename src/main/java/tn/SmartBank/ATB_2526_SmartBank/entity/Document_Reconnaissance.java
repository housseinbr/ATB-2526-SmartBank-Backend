package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_reconnaissance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document_Reconnaissance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document_reconnaissance")
    private Long idDocumentReconnaissance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande_reconnaissance", nullable = false, unique = true)
    private Demande_Reconnaissance demande;

    @Column(nullable = false)
    private String pdfLink;

    private LocalDateTime generatedAt;
}
