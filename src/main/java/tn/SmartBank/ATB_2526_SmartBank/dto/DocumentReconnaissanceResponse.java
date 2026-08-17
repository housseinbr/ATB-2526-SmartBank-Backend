package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Document_Reconnaissance;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReconnaissanceResponse {
    private Long idDocumentReconnaissance;
    private Long idDemandeReconnaissance;
    private String pdfLink;
    private LocalDateTime generatedAt;

    public static DocumentReconnaissanceResponse fromEntity(Document_Reconnaissance document) {
        return DocumentReconnaissanceResponse.builder()
                .idDocumentReconnaissance(document.getIdDocumentReconnaissance())
                .idDemandeReconnaissance(document.getDemande() != null ? document.getDemande().getIdDemandeReconnaissance() : null)
                .pdfLink(document.getPdfLink())
                .generatedAt(document.getGeneratedAt())
                .build();
    }
}
