package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;

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

    @Enumerated(EnumType.STRING)
    private Status status;
}
