package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private String situationEmploye;

    @Column(name = "cathegorie_situation")
    private String cathegorieSituation;

    // Enum in diagram
    private String classification;

    // Enum in diagram
    private String qualification;

    @Column(name = "date_inscrit")
    private LocalDate dateInscrit;
}
