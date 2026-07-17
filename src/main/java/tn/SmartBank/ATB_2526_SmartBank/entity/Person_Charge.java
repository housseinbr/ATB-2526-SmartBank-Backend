package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person_charge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person_Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_person")
    private Long idPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private String name;

    @Column(name = "last_name")
    private String lastName;

    private String relation;

    @Column(name = "num_tel")
    private String numTel;
}
