package tn.SmartBank.ATB_2526_SmartBank.service;

import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // ← service mail à créer

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PWD_LENGTH = 12;

    @Transactional
    public User createUser(User user) {
        // Vérifier unicité email/CIN
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        if (userRepository.existsByCin(user.getCin())) {
            throw new RuntimeException("CIN déjà utilisé");
        }

        // Générer mot de passe aléatoire
        String rawPassword = generateRandomPassword();
        user.setPwd(passwordEncoder.encode(rawPassword));

        User saved = userRepository.save(user);

        // Envoyer le mot de passe par mail
        emailService.sendPasswordEmail(saved.getEmail(), saved.getFirstName(), rawPassword);

        return saved;
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PWD_LENGTH);
        for (int i = 0; i < PWD_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }


    public User updateUser(Long id, User user) {
        User existing = getUserById(id);

        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setUseName(user.getUseName());
        existing.setNumTel(user.getNumTel());
        existing.setNumFax(user.getNumFax());
        existing.setBirthday(user.getBirthday());
        existing.setSexe(user.getSexe());
        existing.setRole(user.getRole());
        existing.setSolde(user.getSolde());
        existing.setSalaire(user.getSalaire());

        // email et cin : mis à jour uniquement s'ils changent, avec re-vérification d'unicité
        if (!existing.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà : " + user.getEmail());
            }
            existing.setEmail(user.getEmail());
        }
        if (!existing.getCin().equals(user.getCin())) {
            if (userRepository.existsByCin(user.getCin())) {
                throw new RuntimeException("Un utilisateur avec ce CIN existe déjà : " + user.getCin());
            }
            existing.setCin(user.getCin());
        }

        return userRepository.save(existing);
    }


    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }


    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + id));
    }


    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }


    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'email : " + email));
    }


    @Transactional(readOnly = true)
    public User getUserByCin(String cin) {
        return userRepository.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec le CIN : " + cin));
    }


    @Transactional(readOnly = true)
    public List<User> getSubordonnes(Long idSuperviseur) {
        return userRepository.findBySuperviseur_Id(idSuperviseur);
    }


    public User assignSuperviseur(Long idUser, Long idSuperviseur) {
        User user = getUserById(idUser);
        User superviseur = getUserById(idSuperviseur);

        if (superviseur.getRole() != Role.SUPERVISEUR && superviseur.getRole() != Role.ADMIN) {
            throw new RuntimeException("L'utilisateur désigné n'a pas le rôle SUPERVISEUR ou ADMIN.");
        }
        if (idUser.equals(idSuperviseur)) {
            throw new RuntimeException("Un utilisateur ne peut pas être son propre superviseur.");
        }

        user.setSuperviseur(superviseur);
        return userRepository.save(user);
    }


    public User changePassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPwd(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
