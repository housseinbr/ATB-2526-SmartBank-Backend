package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_c")
    private Long idC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private String nature;

    // Enum in diagram - e.g. "CDI" / "CDD" / "STAGE"
    @Column(name = "type_contra")
    private String typeContra;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    // Enum in diagram - e.g. "TEMPS_PLEIN" / "TEMPS_PARTIEL"
    @Column(name = "type_temp")
    private String typeTemp;

    @Column(name = "date_affectation")
    private LocalDate dateAffectation;

    // Enum in diagram
    private String post;

    // Enum in diagram
    private String emploi;

    private Double taux;

    private String lieu;
}
