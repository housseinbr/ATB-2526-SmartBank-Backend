package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;

@Entity
@Table(name = "demande_mobilite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande_Mobilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long idDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mobilter", nullable = false)
    private Mobilite mobilite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
