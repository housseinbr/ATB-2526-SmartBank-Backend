package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.entity.History_Sold;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.History_SoldRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // ← service mail à créer
    private final History_SoldRepository historySoldRepository;

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
        if (user.getRole() == Role.EMPLOYE || user.getRole() == Role.SUPERVISEUR) {
            user.setSolde(22d);
        }

        User saved = userRepository.save(user);
        saved = initializeLeaveBalance(saved);

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

        if (user.getFirstName() != null) existing.setFirstName(user.getFirstName());
        if (user.getLastName() != null) existing.setLastName(user.getLastName());
        if (user.getUseName() != null) existing.setUseName(user.getUseName());
        if (user.getNumTel() != null) existing.setNumTel(user.getNumTel());
        if (user.getNumFax() != null) existing.setNumFax(user.getNumFax());
        if (user.getBirthday() != null) existing.setBirthday(user.getBirthday());
        if (user.getSexe() != null) existing.setSexe(user.getSexe());
        if (user.getRole() != null) existing.setRole(user.getRole());
        if (user.getSolde() != null) existing.setSolde(user.getSolde());
        if (user.getSalaire() != null) existing.setSalaire(user.getSalaire());

        if (user.getEmail() != null && !existing.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà : " + user.getEmail());
            }
            existing.setEmail(user.getEmail());
        }
        if (user.getCin() != null && !existing.getCin().equals(user.getCin())) {
            if (userRepository.existsByCin(user.getCin())) {
                throw new RuntimeException("Un utilisateur avec ce CIN existe déjà : " + user.getCin());
            }
            existing.setCin(user.getCin());
        }

        return userRepository.save(existing);
    }


    public void deleteUser(Long id) {
        User user = getUserById(id);

        // Si l'utilisateur est superviseur d'une équipe, détacher ses subordonnés
        List<User> subordonnes = userRepository.findBySuperviseur_Id(id);
        if (!subordonnes.isEmpty()) {
            for (User subordonne : subordonnes) {
                subordonne.setSuperviseur(null);
            }
            userRepository.saveAll(subordonnes);
        }

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

        // Retirer le superviseur
        if (idSuperviseur == null || idSuperviseur == 0) {
            user.setSuperviseur(null);
            return userRepository.save(user);
        }

        // Vérifier qu'on n'assigne pas l'utilisateur à lui-même
        if (idUser.equals(idSuperviseur)) {
            throw new RuntimeException("Un utilisateur ne peut pas être son propre superviseur.");
        }

        User superviseur = getUserById(idSuperviseur);

        // Vérifier que le superviseur a le bon rôle
        if (superviseur.getRole() != Role.SUPERVISEUR && superviseur.getRole() != Role.ADMIN) {
            throw new RuntimeException("L'utilisateur désigné n'a pas le rôle SUPERVISEUR ou ADMIN.");
        }

        user.setSuperviseur(superviseur);
        return userRepository.save(user);
    }


    public User changePassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPwd(passwordEncoder.encode(newPassword));
        User saved = userRepository.save(user);

        emailService.sendPasswordChangedEmail(saved.getEmail(), saved.getFirstName());

        return saved;
    }



    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        String newPassword = generateRandomPassword();
        user.setPwd(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendForgotPasswordEmail(user.getEmail(), user.getFirstName(), newPassword);
    }

    @Transactional
    public User initializeLeaveBalance(User user) {
        if (user.getRole() != Role.EMPLOYE && user.getRole() != Role.SUPERVISEUR) {
            return user;
        }

        if (user.getSolde() == null) {
            user.setSolde(22d);
            user = userRepository.save(user);
        }

        boolean hasHistory = !historySoldRepository.findByUser_IdOrderByDateActionDesc(user.getId()).isEmpty();
        if (!hasHistory) {
            historySoldRepository.save(History_Sold.builder()
                    .user(user)
                    .motif("SOLDE_INITIAL")
                    .dateAction(LocalDate.now())
                    .soldeBefore(0d)
                    .soldeAfter(user.getSolde())
                    .build());
        }

        return user;
    }


}
