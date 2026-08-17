package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Mobilite;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobiliteDto {
    private Long idMobilter;
    private String pays;
    private String societe;
    private String domain;
    private String emploi;
    private Integer unite;
    private String post;
    private LocalDate date;

    public static MobiliteDto fromEntity(Mobilite mobilite) {
        return MobiliteDto.builder()
                .idMobilter(mobilite.getIdMobilter())
                .pays(mobilite.getPays())
                .societe(mobilite.getSociete())
                .domain(mobilite.getDomain())
                .emploi(mobilite.getEmploi())
                .unite(mobilite.getUnite())
                .post(mobilite.getPost())
                .date(mobilite.getDate())
                .build();
    }
}
