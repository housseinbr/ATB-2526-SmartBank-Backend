package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Reconnaissance;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeReconnaissanceResponse {
    private Long idDemandeReconnaissance;
    private UserResponse user;
    private UserResponse superviseur;
    private Type_Demande type;
    private String motif;
    private Status status;
    private LocalDate date;
    private String pdfLink;

    public static DemandeReconnaissanceResponse fromEntity(Demande_Reconnaissance demande) {
        return DemandeReconnaissanceResponse.builder()
                .idDemandeReconnaissance(demande.getIdDemandeReconnaissance())
                .user(demande.getUser() != null ? UserResponse.fromEntity(demande.getUser()) : null)
                .superviseur(demande.getSuperviseur() != null ? UserResponse.fromEntity(demande.getSuperviseur()) : null)
                .type(demande.getType())
                .motif(demande.getMotif())
                .status(demande.getStatus())
                .date(demande.getDate())
                .pdfLink(demande.getPdfLink())
                .build();
    }
}
