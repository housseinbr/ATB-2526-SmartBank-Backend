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


    public void sendPasswordChangedEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ATB SmartBank - Mot de passe modifié");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Nous vous confirmons que le mot de passe de votre compte ATB SmartBank vient d'être modifié.\n\n" +
                        "Si vous n'êtes pas à l'origine de cette action, contactez immédiatement votre administrateur.\n\n" +
                        "Cordialement,\nL'équipe ATB SmartBank"
        );
        mailSender.send(message);
    }

    public void sendForgotPasswordEmail(String to, String firstName, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ATB SmartBank - Réinitialisation de votre mot de passe");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
                        "Voici votre nouveau mot de passe temporaire :\n" +
                        "• Mot de passe : " + newPassword + "\n\n" +
                        "Pour des raisons de sécurité, veuillez le modifier dès votre prochaine connexion " +
                        "(section Profil > Changer mot de passe).\n\n" +
                        "Si vous n'êtes pas à l'origine de cette demande, contactez immédiatement votre administrateur.\n\n" +
                        "Cordialement,\nL'équipe ATB SmartBank"
        );
        mailSender.send(message);
    }
}