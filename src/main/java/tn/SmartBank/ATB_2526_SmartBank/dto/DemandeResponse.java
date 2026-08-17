package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeResponse {
    private Long idDemande;
    private UserResponse user;
    private Type_Demande type;
    private Status status;
    private LocalDate date;

    public static DemandeResponse fromEntity(Demande demande) {
        return DemandeResponse.builder()
                .idDemande(demande.getIdDemande())
                .user(demande.getUser() != null ? UserResponse.fromEntity(demande.getUser()) : null)
                .type(demande.getType())
                .status(demande.getStatus())
                .date(demande.getDate())
                .build();
    }
}
