package tn.SmartBank.ATB_2526_SmartBank.dto;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le CIN est obligatoire")
    private String cin;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String useName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String numTel;
    private String numFax;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    private LocalDate birthday;
    private String sexe;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    private Double salaire;
}
