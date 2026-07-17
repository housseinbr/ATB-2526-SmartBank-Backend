package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.*;

import java.time.LocalDate;

@Entity
@Table(name = "donner_administratif")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donner_Administratif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ad")
    private Long idAd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // Enum in diagram
    @Column(name = "situation_employe")
    private SituationEmploye situationEmploye;

    @Column(name = "cathegorie_situation")
    private CathegorieSituation  cathegorieSituation;

    // Enum in diagram
    @Enumerated(EnumType.STRING)
    private Classification classification;
    // Enum in diagram
    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    @Column(name = "date_inscrit")
    private LocalDate dateInscrit;
}
