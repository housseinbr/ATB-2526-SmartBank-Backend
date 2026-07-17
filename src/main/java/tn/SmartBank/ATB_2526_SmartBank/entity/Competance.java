package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competance")
    private Long idCompetance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;
}
