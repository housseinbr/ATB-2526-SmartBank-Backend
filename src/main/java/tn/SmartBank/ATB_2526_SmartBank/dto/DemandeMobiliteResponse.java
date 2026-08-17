package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Mobilite;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeMobiliteResponse {
    private Long idDemande;
    private MobiliteDto mobilite;
    private UserResponse user;
    private Status status;
    private LocalDate date;

    public static DemandeMobiliteResponse fromEntity(Demande_Mobilite demande) {
        return DemandeMobiliteResponse.builder()
                .idDemande(demande.getIdDemande())
                .mobilite(demande.getMobilite() != null ? MobiliteDto.fromEntity(demande.getMobilite()) : null)
                .user(demande.getUser() != null ? UserResponse.fromEntity(demande.getUser()) : null)
                .status(demande.getStatus())
                .date(demande.getDate())
                .build();
    }
}
