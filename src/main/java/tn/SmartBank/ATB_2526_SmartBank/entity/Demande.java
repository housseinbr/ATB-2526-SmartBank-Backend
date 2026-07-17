package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "demande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long idDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // Enum in diagram - e.g. "CONGE" / "MOBILITE" / "FORMATION"
    private String type;

    private LocalDate date;

    // Enum in diagram - e.g. "EN_ATTENTE" / "ACCEPTEE" / "REFUSEE"
    private String status;
}
