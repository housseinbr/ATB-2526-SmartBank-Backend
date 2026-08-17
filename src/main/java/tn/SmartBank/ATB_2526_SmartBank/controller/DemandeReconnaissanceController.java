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
import tn.SmartBank.ATB_2526_SmartBank.dto.DemandeReconnaissanceResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.DocumentReconnaissanceResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Reconnaissance;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.DemandeReconnaissanceService;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-reconnaissance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class DemandeReconnaissanceController {

    private final DemandeReconnaissanceService service;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeReconnaissanceResponse>> me(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(service.getMyRequests(user.getId()).stream().map(DemandeReconnaissanceResponse::fromEntity).toList());
    }

    @GetMapping("/managed")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeReconnaissanceResponse>> managed(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(service.getManagedRequests(user.getId()).stream().map(DemandeReconnaissanceResponse::fromEntity).toList());
    }

    @GetMapping("/managed/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeReconnaissanceResponse>> pending(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(service.getManagedPendingRequests(user.getId()).stream().map(DemandeReconnaissanceResponse::fromEntity).toList());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<DemandeReconnaissanceResponse> create(
            @PathVariable Long userId,
            @RequestParam Type_Demande type,
            @RequestParam(required = false) String motif) {
        Demande_Reconnaissance created = service.create(userId, type, motif);
        return ResponseEntity.status(HttpStatus.CREATED).body(DemandeReconnaissanceResponse.fromEntity(created));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<DemandeReconnaissanceResponse> decide(@PathVariable Long id, @PathVariable Status decision, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(DemandeReconnaissanceResponse.fromEntity(service.decide(id, user.getId(), decision)));
    }

    @GetMapping("/{demandeId}/document")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<DocumentReconnaissanceResponse> generated(@PathVariable Long demandeId) {
        return ResponseEntity.ok(DocumentReconnaissanceResponse.fromEntity(service.getGeneratedDocument(demandeId)));
    }
}
