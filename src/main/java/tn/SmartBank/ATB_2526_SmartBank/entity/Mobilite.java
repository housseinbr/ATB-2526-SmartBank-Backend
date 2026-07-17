package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "mobilite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mobilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mobilter")
    private Long idMobilter;

    private String pays;

    private String societe;
    private String domain;

    private String emploi;

    private Integer unite;

    private String post;

    private LocalDate date;
}
