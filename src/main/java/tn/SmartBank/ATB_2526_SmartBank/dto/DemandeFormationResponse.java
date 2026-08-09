package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Formation;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeFormationResponse {
    private Long idDemandeFormation;
    private FormationResponse formation;
    private UserResponse user;
    private Status status;
    private LocalDate date;

    public static DemandeFormationResponse fromEntity(Demande_Formation demandeFormation) {
        return DemandeFormationResponse.builder()
                .idDemandeFormation(demandeFormation.getIdDemandeFormation())
                .formation(demandeFormation.getFormation() != null ? FormationResponse.fromEntity(demandeFormation.getFormation()) : null)
                .user(demandeFormation.getUser() != null ? UserResponse.fromEntity(demandeFormation.getUser()) : null)
                .status(demandeFormation.getStatus())
                .date(demandeFormation.getDate())
                .build();
    }
}
