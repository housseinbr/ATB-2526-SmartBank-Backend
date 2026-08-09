package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Competance;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetanceResponse {
    private Long idCompetance;
    private UserResponse user;
    private FormationResponse formation;

    public static CompetanceResponse fromEntity(Competance competance) {
        return CompetanceResponse.builder()
                .idCompetance(competance.getIdCompetance())
                .user(competance.getUser() != null ? UserResponse.fromEntity(competance.getUser()) : null)
                .formation(competance.getFormation() != null ? FormationResponse.fromEntity(competance.getFormation()) : null)
                .build();
    }
}
