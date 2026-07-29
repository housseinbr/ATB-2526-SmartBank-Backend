package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.DemiJournee;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_abs;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AbcenceResponse {
    private Long idAbcance;
    private UserResponse user;
    private Type_abs type;
    private String comment;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private DemiJournee demiJournee;
    private Status status;

    public static AbcenceResponse fromEntity(Abcence abcence) {
        return AbcenceResponse.builder()
                .idAbcance(abcence.getIdAbcance())
                .user(abcence.getUser() != null ? UserResponse.fromEntity(abcence.getUser()) : null)
                .type(abcence.getType())
                .comment(abcence.getComment())
                .dateStart(abcence.getDateStart())
                .dateEnd(abcence.getDateEnd())
                .demiJournee(abcence.getDemiJournee())
                .status(abcence.getStatus())
                .build();
    }
}
