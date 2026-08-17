package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.dto.EvaluationResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Evaluation;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.EvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EvaluationResponse>> me(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(evaluationService.getMyEvaluations(user.getId()).stream().map(EvaluationResponse::fromEntity).toList());
    }

    @GetMapping("/managed")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EvaluationResponse>> managed(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(evaluationService.getManagedEvaluations(user.getId()).stream().map(EvaluationResponse::fromEntity).toList());
    }

    @GetMapping("/managed/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EvaluationResponse>> pending(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(evaluationService.getManagedPendingEvaluations(user.getId()).stream().map(EvaluationResponse::fromEntity).toList());
    }

    @PostMapping("/user/{userId}/supervisor/{supervisorId}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<EvaluationResponse> create(
            @PathVariable Long userId,
            @PathVariable Long supervisorId,
            @RequestBody Evaluation evaluation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(EvaluationResponse.fromEntity(evaluationService.create(userId, supervisorId, evaluation)));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<EvaluationResponse> decide(@PathVariable Long id, @PathVariable Status decision, Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(EvaluationResponse.fromEntity(evaluationService.decide(id, user.getId(), decision)));
    }
}
