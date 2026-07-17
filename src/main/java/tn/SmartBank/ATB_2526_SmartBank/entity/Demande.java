package tn.SmartBank.ATB_2526_SmartBank.entity;
import tn.SmartBank.ATB_2526_SmartBank.Enums.*;

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

    // Enum in diagram - e.g. "ATTESTATION" / "BADGES"
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type_Demande type;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
