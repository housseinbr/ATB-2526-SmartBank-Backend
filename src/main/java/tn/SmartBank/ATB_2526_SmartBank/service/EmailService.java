package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordEmail(String to, String firstName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre compte ATB SmartBank — Identifiants de connexion");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre compte a été créé avec succès.\n\n" +
                        "Voici vos identifiants de connexion :\n" +
                        "• Email : " + to + "\n" +
                        "• Mot de passe temporaire : " + password + "\n\n" +
                        "Pour des raisons de sécurité, veuillez changer votre mot de passe dès votre première connexion.\n\n" +
                        "Cordialement,\nL'équipe ATB SmartBank"
        );
        mailSender.send(message);
    }
}