package tn.SmartBank.ATB_2526_SmartBank.dto;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
