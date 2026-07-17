package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluation")
    private Long idEvaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private String title;

    @Column(name = "desc_", columnDefinition = "TEXT")
    private String desc;

    private LocalDate date;

    private String lieu;

    // Enum in diagram
    private String status;
}
