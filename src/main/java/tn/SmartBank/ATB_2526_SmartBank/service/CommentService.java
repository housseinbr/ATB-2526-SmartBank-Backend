package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.entity.Comment;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.CommentRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return commentRepository.findAll().stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForUser(Long userId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getUser() != null && userId.equals(comment.getUser().getId()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
    }

    public Comment createComment(Long authorId, String text) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Comment comment = Comment.builder()
                .user(author)
                .text(text)
                .date(LocalDate.now())
                .build();

        Comment saved = commentRepository.save(comment);
        notifyRecipients(author, text);
        return saved;
    }

    private void notifyRecipients(User author, String text) {
        String subject = "Nouveau commentaire";
        String message = author.getFirstName() + " " + author.getLastName() + " a ajouté un commentaire : " + text;

        if (author.getRole() == Role.EMPLOYE) {
            if (author.getSuperviseur() != null) {
                notificationService.create(author.getSuperviseur(), subject, message);
            }
            userRepository.findByRole(Role.ADMIN).forEach(admin ->
                    notificationService.create(admin, subject, message)
            );
            return;
        }

        if (author.getRole() == Role.SUPERVISEUR) {
            userRepository.findByRole(Role.ADMIN).forEach(admin ->
                    notificationService.create(admin, subject, message)
            );
            return;
        }

        if (author.getRole() == Role.ADMIN) {
            userRepository.findByRole(Role.SUPERVISEUR).forEach(supervisor ->
                    notificationService.create(supervisor, subject, message)
            );
        }
    }
}
