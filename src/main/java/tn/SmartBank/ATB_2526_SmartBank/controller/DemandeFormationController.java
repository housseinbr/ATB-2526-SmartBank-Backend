package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.dto.DemandeFormationResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Formation;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.DemandeFormationService;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-formations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class DemandeFormationController {

    private final DemandeFormationService demandeFormationService;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeFormationResponse>> myRequests(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                demandeFormationService.getMyRequests(userDetails.getId()).stream()
                        .map(DemandeFormationResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/managed")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeFormationResponse>> managed(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                demandeFormationService.getManagedRequests(userDetails.getId()).stream()
                        .map(DemandeFormationResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/managed/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeFormationResponse>> pending(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                demandeFormationService.getManagedPendingRequests(userDetails.getId()).stream()
                        .map(DemandeFormationResponse::fromEntity)
                        .toList()
        );
    }

    @PostMapping("/formation/{formationId}")
    @Transactional
    public ResponseEntity<DemandeFormationResponse> create(
            @PathVariable Long formationId,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Demande_Formation created = demandeFormationService.create(formationId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(DemandeFormationResponse.fromEntity(created));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional
    public ResponseEntity<DemandeFormationResponse> decide(
            @PathVariable Long id,
            @PathVariable Status decision,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                DemandeFormationResponse.fromEntity(
                        demandeFormationService.decide(id, userDetails.getId(), decision)
                )
        );
    }
}
