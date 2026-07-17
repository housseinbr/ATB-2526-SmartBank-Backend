package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bank_Account {

    // One-to-one with User: Id_User acts as PK in the diagram
    @Id
    @Column(name = "id_user")
    private Long idUser;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "name_benifice")
    private String nameBenifice;

    @Column(name = "bank_title")
    private String bankTitle;

    private String ville;

    private String compte;

    @Column(name = "controlle_chiffre")
    private Float controlleChiffre;

    private String contry;
}
