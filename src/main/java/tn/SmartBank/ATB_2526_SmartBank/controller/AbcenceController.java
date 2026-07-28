package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.AbsenceService;

import java.util.List;

@RestController
@RequestMapping("/api/abcences")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class AbcenceController {

    private final AbsenceService absenceService;

    // CREATE — lie automatiquement l'absence à l'utilisateur connecté
    @PostMapping
    public ResponseEntity<Abcence> create(@RequestBody Abcence abcence, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        abcence.setUser(buildUserRef(userDetails.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(absenceService.create(abcence));
    }

    // GET ALL — un employé ne voit que SES absences ; admin/superviseur voit tout
    @GetMapping
    public ResponseEntity<List<Abcence>> getAll(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        boolean isEmploye = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYE"));

        if (isEmploye) {
            return ResponseEntity.ok(absenceService.findByUserId(userDetails.getId()));
        }
        return ResponseEntity.ok(absenceService.getAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Abcence> getById(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Abcence> update(
            @PathVariable Long id,
            @RequestBody Abcence abcence,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(absenceService.update(id, abcence, userDetails.getId()));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        absenceService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Helper pour créer une référence User sans charger l'entité complète
    private User buildUserRef(Long id) {
        User u = new User();
        u.setId(id);
        return u;
    }
}