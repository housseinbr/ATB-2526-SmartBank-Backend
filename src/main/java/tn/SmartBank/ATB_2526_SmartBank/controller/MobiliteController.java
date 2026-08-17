package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.dto.MobiliteDto;
import tn.SmartBank.ATB_2526_SmartBank.entity.Mobilite;
import tn.SmartBank.ATB_2526_SmartBank.service.MobiliteService;

import java.util.List;

@RestController
@RequestMapping("/api/mobilites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class MobiliteController {

    private final MobiliteService mobiliteService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<MobiliteDto>> getAll() {
        return ResponseEntity.ok(mobiliteService.getAll().stream().map(MobiliteDto::fromEntity).toList());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<MobiliteDto> create(@RequestBody Mobilite mobilite) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MobiliteDto.fromEntity(mobiliteService.save(mobilite)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mobiliteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
