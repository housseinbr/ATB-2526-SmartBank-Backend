package tn.SmartBank.ATB_2526_SmartBank.entity;
import tn.SmartBank.ATB_2526_SmartBank.Enums.*;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "abcence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abcence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abcance")
    private Long idAbcance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // Enum in diagram - e.g. "CONGE_PAYE" / "MALADIE" / "SANS_SOLDE"
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type_abs type;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "demi_journee")
    private DemiJournee demiJournee;

    // Enum in diagram, labelled "A voir" - e.g. "EN_ATTENTE" / "VALIDEE" / "REJETEE"
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
