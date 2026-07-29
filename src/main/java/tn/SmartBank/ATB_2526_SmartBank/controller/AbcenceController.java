package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.dto.AbcenceResponse;
import tn.SmartBank.ATB_2526_SmartBank.dto.HistorySoldResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.AbsenceService;

import java.util.List;

@RestController
@RequestMapping("/api/abcences")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class AbcenceController {

    private final AbsenceService absenceService;

    @PostMapping
    public ResponseEntity<AbcenceResponse> create(@RequestBody Abcence abcence, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Abcence created = absenceService.create(abcence, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AbcenceResponse.fromEntity(created));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AbcenceResponse>> getMyAbsences(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                absenceService.findMyAbsences(userDetails.getId()).stream()
                        .map(AbcenceResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<List<AbcenceResponse>> getTeamAbsences(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                absenceService.findTeamAbsences(userDetails.getId()).stream()
                        .map(AbcenceResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/team/pending")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<List<AbcenceResponse>> getPendingTeamAbsences(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                absenceService.findTeamPendingAbsences(userDetails.getId()).stream()
                        .map(AbcenceResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/history/me")
    public ResponseEntity<List<HistorySoldResponse>> getMyHistory(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                absenceService.findHistoryForUser(userDetails.getId()).stream()
                        .map(HistorySoldResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbcenceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AbcenceResponse.fromEntity(absenceService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbcenceResponse> update(
            @PathVariable Long id,
            @RequestBody Abcence abcence,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Abcence updated = absenceService.update(id, abcence, userDetails.getId());
        return ResponseEntity.ok(AbcenceResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/decision/{decision}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<AbcenceResponse> decide(
            @PathVariable Long id,
            @PathVariable Status decision,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Abcence updated = absenceService.decide(id, userDetails.getId(), decision);
        return ResponseEntity.ok(AbcenceResponse.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        absenceService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
