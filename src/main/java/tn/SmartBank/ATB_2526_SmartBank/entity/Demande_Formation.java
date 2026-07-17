package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;

import java.time.LocalDate;

@Entity
@Table(name = "demande_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande_Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande_formation")
    private Long idDemandeFormation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    private LocalDate date;
}
