package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Formation;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationResponse {
    private Long idFormation;
    private String title;
    private String offreFormation;
    private String domain;
    private String theme;
    private Integer duree;
    private String lieu;
    private String unite;

    public static FormationResponse fromEntity(Formation formation) {
        return FormationResponse.builder()
                .idFormation(formation.getIdFormation())
                .title(formation.getTitle())
                .offreFormation(formation.getOffreFormation())
                .domain(formation.getDomain())
                .theme(formation.getTheme())
                .duree(formation.getDuree())
                .lieu(formation.getLieu())
                .unite(formation.getUnite())
                .build();
    }
}
