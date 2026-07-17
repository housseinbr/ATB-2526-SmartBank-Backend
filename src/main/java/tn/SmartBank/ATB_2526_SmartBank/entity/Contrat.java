package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import tn.SmartBank.ATB_2526_SmartBank.Enums.*;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "nature")
    private Nature_Contrat nature;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contra")
    private Type_Contrat typeContra;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_temp")
    private Type_Temp typeTemp;

    @Column(name = "date_affectation")
    private LocalDate dateAffectation;

    @Enumerated(EnumType.STRING)
    @Column(name = "post")
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "emploi")
    private Emploi emploi;

    private Double taux;

    private String lieu;
}
