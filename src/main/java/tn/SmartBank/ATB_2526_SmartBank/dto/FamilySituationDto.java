package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.Familly_situation;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilySituationDto {
    private Long idUser;
    private String situation;
    private String documentUpload;
    private String documentLink;

    public static FamilySituationDto fromEntity(Familly_situation situation) {
        return FamilySituationDto.builder()
                .idUser(situation.getIdUser())
                .situation(situation.getSituation())
                .documentUpload(situation.getDocumentUpload() != null ? situation.getDocumentUpload().name() : null)
                .documentLink(situation.getDocumentLink())
                .build();
    }
}
