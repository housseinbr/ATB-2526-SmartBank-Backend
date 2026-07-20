package tn.SmartBank.ATB_2526_SmartBank.controller;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.dto.UserResponse;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.fromEntity(userService.getUserById(id)));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.getUsersByRole(role).stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/subordonnes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<List<UserResponse>> getSubordonnes(@PathVariable Long id) {
        List<UserResponse> users = userService.getSubordonnes(id).stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(UserResponse.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleActive(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {  // ← String, pas Boolean
        User user = userService.getUserById(id);
        String newStatus = body.get("actif");
        if (!"actif".equals(newStatus) && !"inactif".equals(newStatus)) {
            return ResponseEntity.badRequest().build();
        }
        user.setActif(newStatus);
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    @PatchMapping("/{idUser}/superviseur/{idSuperviseur}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignSuperviseur(
            @PathVariable Long idUser,
            @PathVariable Long idSuperviseur) {
        User updated = userService.assignSuperviseur(idUser, idSuperviseur);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User user = userService.getUserById(id);
        String newStatus = body.get("actif");
        if (!"actif".equals(newStatus) && !"inactif".equals(newStatus)) {
            return ResponseEntity.badRequest().build();
        }
        user.setActif(newStatus);
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }


}