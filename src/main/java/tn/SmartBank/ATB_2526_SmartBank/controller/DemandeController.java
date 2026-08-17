package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;
import tn.SmartBank.ATB_2526_SmartBank.dto.DemandeResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.DemandeService;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class DemandeController {

    private final DemandeService demandeService;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeResponse>> me(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeService.getMyRequests(user.getId()).stream().map(DemandeResponse::fromEntity).toList());
    }

    @GetMapping("/managed")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeResponse>> managed(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeService.getManagedRequests(user.getId()).stream().map(DemandeResponse::fromEntity).toList());
    }

    @GetMapping("/managed/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeResponse>> pending(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeService.getManagedPendingRequests(user.getId()).stream().map(DemandeResponse::fromEntity).toList());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<DemandeResponse> create(
            @PathVariable Long userId,
            @RequestParam Type_Demande type) {
        Demande created = demandeService.create(userId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(DemandeResponse.fromEntity(created));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<DemandeResponse> decide(@PathVariable Long id, @PathVariable Status decision, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(DemandeResponse.fromEntity(demandeService.decide(id, user.getId(), decision)));
    }
}
