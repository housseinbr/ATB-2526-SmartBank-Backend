package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.dto.FormationRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.FormationResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.Formation;
import tn.SmartBank.ATB_2526_SmartBank.service.FormationService;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class FormationController {

    private final FormationService formationService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<FormationResponse>> getAll() {
        return ResponseEntity.ok(
                formationService.getAll().stream()
                        .map(FormationResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<FormationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(FormationResponse.fromEntity(formationService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional
    public ResponseEntity<FormationResponse> create(@RequestBody FormationRequest request) {
        Formation created = formationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(FormationResponse.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional
    public ResponseEntity<FormationResponse> update(@PathVariable Long id, @RequestBody FormationRequest request) {
        return ResponseEntity.ok(FormationResponse.fromEntity(formationService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        formationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
