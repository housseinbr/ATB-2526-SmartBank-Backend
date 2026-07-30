package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.SmartBank.ATB_2526_SmartBank.dto.CommentRequest;
import tn.SmartBank.ATB_2526_SmartBank.dto.CommentResponse;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/me")
    public ResponseEntity<List<CommentResponse>> myComments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                commentService.getCommentsForUser(userDetails.getId()).stream()
                        .map(CommentResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponse>> allComments() {
        return ResponseEntity.ok(
                commentService.getAllComments().stream()
                        .map(CommentResponse::fromEntity)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @RequestBody CommentRequest request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                CommentResponse.fromEntity(commentService.createComment(userDetails.getId(), request.getText()))
        );
    }
}
