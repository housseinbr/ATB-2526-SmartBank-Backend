package tn.SmartBank.ATB_2526_SmartBank.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.service.AbsenceService;

import java.util.List;

@RestController
@RequestMapping("/api/abcences")
@RequiredArgsConstructor
public class AbcenceController {

    private final AbsenceService abcenceService;

    // CREATE
    @PostMapping
    public ResponseEntity<Abcence> create(
            @RequestBody Abcence abcence
    ) {

        Abcence createdAbcence =
                abcenceService.create(abcence);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAbcence);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Abcence>> getAll() {

        return ResponseEntity.ok(
                abcenceService.getAll()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Abcence> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                abcenceService.getById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Abcence> update(
            @PathVariable Long id,
            @RequestBody Abcence abcence
    ) {

        return ResponseEntity.ok(
                abcenceService.update(id, abcence)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        abcenceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
