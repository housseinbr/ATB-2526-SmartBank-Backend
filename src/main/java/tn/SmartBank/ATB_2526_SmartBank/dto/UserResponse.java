package tn.SmartBank.ATB_2526_SmartBank.dto;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String cin;
    private String firstName;
    private String lastName;
    private String useName;
    private String email;
    private String numTel;
    private String numFax;
    private LocalDate birthday;
    private String sexe;
    private Role role;
    private Double solde;
    private Double salaire;
    private Long idSuperviseur;
    private String actif;  // ← CHANGÉ : String (pas boolean)

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .cin(user.getCin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .useName(user.getUseName())
                .email(user.getEmail())
                .numTel(user.getNumTel())
                .numFax(user.getNumFax())
                .birthday(user.getBirthday())
                .sexe(user.getSexe())
                .role(user.getRole())
                .solde(user.getSolde())
                .salaire(user.getSalaire())
                .idSuperviseur(user.getSuperviseur() != null ? user.getSuperviseur().getId() : null)
                .actif(user.getActif())  // ← Maintenant ça compile : String → String
                .build();
    }
}