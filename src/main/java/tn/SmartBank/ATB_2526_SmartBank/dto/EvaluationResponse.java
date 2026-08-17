package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Evaluation;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long idEvaluation;
    private UserResponse user;
    private UserResponse superviseur;
    private String title;
    private String desc;
    private LocalDate date;
    private String lieu;
    private Status status;

    public static EvaluationResponse fromEntity(Evaluation evaluation) {
        return EvaluationResponse.builder()
                .idEvaluation(evaluation.getIdEvaluation())
                .user(evaluation.getUser() != null ? UserResponse.fromEntity(evaluation.getUser()) : null)
                .superviseur(evaluation.getSuperviseur() != null ? UserResponse.fromEntity(evaluation.getSuperviseur()) : null)
                .title(evaluation.getTitle())
                .desc(evaluation.getDesc())
                .date(evaluation.getDate())
                .lieu(evaluation.getLieu())
                .status(evaluation.getStatus())
                .build();
    }
}
