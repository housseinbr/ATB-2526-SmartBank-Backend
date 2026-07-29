package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.entity.History_Sold;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistorySoldResponse {
    private Long idHistorySold;
    private UserResponse user;
    private Long idAbcence;
    private String motif;
    private LocalDate dateAction;
    private Double soldeBefore;
    private Double soldeAfter;

    public static HistorySoldResponse fromEntity(History_Sold history) {
        return HistorySoldResponse.builder()
                .idHistorySold(history.getIdHistorySold())
                .user(history.getUser() != null ? UserResponse.fromEntity(history.getUser()) : null)
                .idAbcence(history.getAbcence() != null ? history.getAbcence().getIdAbcance() : null)
                .motif(history.getMotif())
                .dateAction(history.getDateAction())
                .soldeBefore(history.getSoldeBefore())
                .soldeAfter(history.getSoldeAfter())
                .build();
    }
}
