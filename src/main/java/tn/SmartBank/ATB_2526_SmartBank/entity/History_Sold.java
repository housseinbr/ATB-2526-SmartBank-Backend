package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "history_sold")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History_Sold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history_sold")
    private Long idHistorySold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_abcence")
    private Abcence abcence;

    // "Aquis / Pris" in the diagram -> what this ledger line represents
    private String motif;

    @Column(name = "date_action")
    private LocalDate dateAction;

    @Column(name = "solde_before")
    private Float soldeBefore;

    @Column(name = "solde_after")
    private Float soldeAfter;
}
