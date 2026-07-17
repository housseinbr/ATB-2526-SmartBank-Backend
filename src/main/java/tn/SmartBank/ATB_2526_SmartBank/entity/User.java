package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cin;

    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "use_name")
    private String useName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "num_tel")
    private String numTel;

    @Column(name = "num_fax")
    private String numFax;

    @Column(name = "pwd", nullable = false)
    private String pwd;

    private LocalDate birthday;

    // Varchar in the diagram - kept as String (e.g. "M" / "F")
    private String sexe;

    // Varchar in the diagram - e.g. "EMPLOYE" / "SUPERVISEUR" / "ADMIN"
    private String role;

    private Double solde;

    // Self-referencing supervisor relationship, nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_superviseur", nullable = true)
    private User superviseur;
}
