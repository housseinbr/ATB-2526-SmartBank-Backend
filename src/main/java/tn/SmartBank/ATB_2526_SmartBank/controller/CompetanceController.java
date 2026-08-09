package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.dto.CompetanceResponse;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.CompetanceService;

import java.util.List;

@RestController
@RequestMapping("/api/competances")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class CompetanceController {

    private final CompetanceService competanceService;

    @GetMapping("/me")
    public ResponseEntity<List<CompetanceResponse>> myCompetances(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                competanceService.getMyCompetances(userDetails.getId()).stream()
                        .map(CompetanceResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompetanceResponse>> userCompetances(@PathVariable Long userId) {
        return ResponseEntity.ok(
                competanceService.getUserCompetances(userId).stream()
                        .map(CompetanceResponse::fromEntity)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
