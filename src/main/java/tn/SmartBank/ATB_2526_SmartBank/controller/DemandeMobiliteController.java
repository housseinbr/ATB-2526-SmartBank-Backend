package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.dto.DemandeMobiliteResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Mobilite;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.DemandeMobiliteService;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-mobilites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class DemandeMobiliteController {

    private final DemandeMobiliteService demandeMobiliteService;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeMobiliteResponse>> me(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeMobiliteService.getMyRequests(user.getId()).stream().map(DemandeMobiliteResponse::fromEntity).toList());
    }

    @GetMapping("/managed")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeMobiliteResponse>> managed(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeMobiliteService.getManagedRequests(user.getId()).stream().map(DemandeMobiliteResponse::fromEntity).toList());
    }

    @GetMapping("/managed/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DemandeMobiliteResponse>> pending(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(demandeMobiliteService.getManagedPendingRequests(user.getId()).stream().map(DemandeMobiliteResponse::fromEntity).toList());
    }

    @PostMapping("/mobilite/{mobiliteId}")
    public ResponseEntity<DemandeMobiliteResponse> create(@PathVariable Long mobiliteId, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        Demande_Mobilite created = demandeMobiliteService.create(mobiliteId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(DemandeMobiliteResponse.fromEntity(created));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<DemandeMobiliteResponse> decide(@PathVariable Long id, @PathVariable Status decision, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(DemandeMobiliteResponse.fromEntity(demandeMobiliteService.decide(id, user.getId(), decision)));
    }
}
