package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;

import java.time.LocalDate;

@Entity
@Table(name = "demande_reconnaissance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande_Reconnaissance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande_reconnaissance")
    private Long idDemandeReconnaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_superviseur")
    private User superviseur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type_Demande type;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private LocalDate date;

    private String pdfLink;
}
