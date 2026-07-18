package tn.SmartBank.ATB_2526_SmartBank.controller;

import tn.SmartBank.ATB_2526_SmartBank.dto.AuthResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.LoginRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.RegisterRequest;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;
import tn.SmartBank.ATB_2526_SmartBank.security.JwtUtils;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtUtils.generateToken(userDetails);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà : " + request.getEmail());
        }
        if (userRepository.existsByCin(request.getCin())) {
            throw new RuntimeException("Un utilisateur avec ce CIN existe déjà : " + request.getCin());
        }

        User user = User.builder()
                .cin(request.getCin())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .useName(request.getUseName())
                .email(request.getEmail())
                .numTel(request.getNumTel())
                .numFax(request.getNumFax())
                .pwd(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .sexe(request.getSexe())
                .role(request.getRole())
                .salaire(request.getSalaire())
                .build();

        User saved = userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(saved);
        String token = jwtUtils.generateToken(userDetails);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .id(saved.getId())
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .role(saved.getRole())
                .build();

        return ResponseEntity.ok(response);
    }
}
