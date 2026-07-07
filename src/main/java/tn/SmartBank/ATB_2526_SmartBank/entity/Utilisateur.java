package tn.SmartBank.ATB_2526_SmartBank.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    public enum Sexe {
        HOMME,
        FEMME,
        AUTRE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "num_tel")
    private String numTel;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email, String numTel, LocalDate dateNaissance, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTel = numTel;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }
}