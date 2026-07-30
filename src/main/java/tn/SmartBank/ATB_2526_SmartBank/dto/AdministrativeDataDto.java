package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Donner_Administratif;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeDataDto {
    private Long idAd;
    private String situationEmploye;
    private String cathegorieSituation;
    private String classification;
    private String qualification;
    private LocalDate dateInscrit;
    private String documentLink;

    public static AdministrativeDataDto fromEntity(Donner_Administratif data) {
        return AdministrativeDataDto.builder()
                .idAd(data.getIdAd())
                .situationEmploye(data.getSituationEmploye() != null ? data.getSituationEmploye().name() : null)
                .cathegorieSituation(data.getCathegorieSituation() != null ? data.getCathegorieSituation().name() : null)
                .classification(data.getClassification() != null ? data.getClassification().name() : null)
                .qualification(data.getQualification() != null ? data.getQualification().name() : null)
                .dateInscrit(data.getDateInscrit())
                .documentLink(data.getDocumentLink())
                .build();
    }
}
