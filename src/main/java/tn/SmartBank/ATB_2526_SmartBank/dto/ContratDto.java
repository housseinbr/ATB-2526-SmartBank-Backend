package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Contrat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratDto {
    private Long idC;
    private String nature;
    private String typeContra;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private String typeTemp;
    private LocalDate dateAffectation;
    private String post;
    private String emploi;
    private Double taux;
    private String lieu;
    private String documentLink;

    public static ContratDto fromEntity(Contrat contrat) {
        if (contrat == null) {
            return null;
        }
        return ContratDto.builder()
                .idC(contrat.getIdC())
                .nature(contrat.getNature() != null ? contrat.getNature().name() : null)
                .typeContra(contrat.getTypeContra() != null ? contrat.getTypeContra().name() : null)
                .dateStart(contrat.getDateStart())
                .dateEnd(contrat.getDateEnd())
                .typeTemp(contrat.getTypeTemp() != null ? contrat.getTypeTemp().name() : null)
                .dateAffectation(contrat.getDateAffectation())
                .post(contrat.getPost() != null ? contrat.getPost().name() : null)
                .emploi(contrat.getEmploi() != null ? contrat.getEmploi().name() : null)
                .taux(contrat.getTaux())
                .lieu(contrat.getLieu())
                .documentLink(contrat.getDocumentLink())
                .build();
    }
}
